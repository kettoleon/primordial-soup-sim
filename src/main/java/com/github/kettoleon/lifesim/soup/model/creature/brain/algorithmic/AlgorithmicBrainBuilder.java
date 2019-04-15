package com.github.kettoleon.lifesim.soup.model.creature.brain.algorithmic;

import com.github.kettoleon.lifesim.soup.model.creature.Creature;
import com.github.kettoleon.lifesim.soup.model.creature.CreatureBuilder;
import com.github.kettoleon.lifesim.soup.model.creature.brain.Brain;
import com.github.kettoleon.lifesim.soup.model.creature.brain.NoBrain;
import com.github.kettoleon.lifesim.soup.model.genetics.Dna;
import com.github.kettoleon.lifesim.soup.model.genetics.DnaReader;
import com.github.kettoleon.lifesim.soup.model.genetics.GeneticBuilder;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.kettoleon.lifesim.soup.util.JavaCompilerUtils.instantiateSourceCode;
import static com.github.kettoleon.lifesim.soup.util.JavaCompilerUtils.uniqueClassNameSuffix;
import static java.util.stream.Collectors.toList;

public class AlgorithmicBrainBuilder implements GeneticBuilder<Brain> {


    private final int inSize;
    private final int outSize;
    private int memSize;
    private Creature creature;

    public AlgorithmicBrainBuilder(Creature creature) {

        this.creature = creature;
        inSize = creature.getInputsSize();
        outSize = creature.getOutputsSize();
    }

    @Override
    public Brain build(DnaReader dna) {
        if (dna.hasMoreGenes()) {
            memSize = dna.nextInt(0, 40);
            BrainAlgorithm brainAlgorithm = buildBrainAlgorithm(dna);
            return new AlgorithmicBrain(new float[memSize], brainAlgorithm);
        }
        return new NoBrain();
    }

    private BrainAlgorithm buildBrainAlgorithm(DnaReader dna) {

        List<Instruction> instructions = new ArrayList<>();

        for (int i = 0; i < outSize; i++) {
            String dest = "o[" + i + "]";
            Optional<Operation> op = dna.next(this::pickOp);
            Optional<String[]> ops = dna.next(this::pickOps);
            if (op.isPresent() && ops.isPresent()) {
                instructions.add(new Instruction(dest, op.get(), ops.get()));
            }
        }

        while (dna.hasMoreGenes()) {
            Optional<String> dest = dna.next(this::pickDest);
            Optional<Operation> op = dna.next(this::pickOp);
            Optional<String[]> ops = dna.next(this::pickOps);
            if (dest.isPresent() && op.isPresent() && ops.isPresent()) {
                instructions.add(new Instruction(dest.get(), op.get(), ops.get()));
            }
        }

        reduce(instructions);

        Collections.reverse(instructions);
        return compileBrainAlgorithm(instructions);
    }

    private void reduce(List<Instruction> instructions) {
        List<Instruction> toRemove = new ArrayList<>();
        toRemove.addAll(instructions); //All guilty unless proven differently

        List<Instruction> outputAssignements = findOutputAssignements(instructions);
        toRemove.removeAll(outputAssignements); //Save output assignements at least

        for (Instruction instruction : outputAssignements) {
            saveDependencies(instructions, toRemove, instruction);
        }

        instructions.removeAll(toRemove);
    }

    private void saveDependencies(List<Instruction> instructions, List<Instruction> toRemove, Instruction instruction) {
        for (String op : instruction.getOperands()) {
            if (op.contains("[") && !op.contains("i")) {
                Optional<Instruction> opAssignement = findPreviousAssignement(instructions, instruction, op);
                if (opAssignement.isPresent()) {
                    toRemove.remove(opAssignement.get());
                    saveDependencies(instructions, toRemove, opAssignement.get());
                }
            }
        }
    }

    private Optional<Instruction> findPreviousAssignement(List<Instruction> instructions, Instruction parent, String op) {
        boolean parentPassed = false;
        for (Instruction current : instructions) {
            if (!parentPassed && parent.equals(current)) {
                parentPassed = true;
            } else if (parentPassed && current.getDest().equals(op)) {
                return Optional.of(current);
            }
        }

        return Optional.empty();
    }

    private List<Instruction> findOutputAssignements(List<Instruction> instructions) {
        return instructions.stream().filter(i -> i.getDest().startsWith("o")).collect(toList());
    }

    private String[] pickOps(DnaReader dna) {

        Optional<String> op1 = dna.next(this::pickOneOp);
        Optional<String> op2 = dna.next(this::pickOneOp);

        if (op1.isPresent() && op2.isPresent()) {
            return new String[]{op1.get(), op2.get()};
        }
        return null;
    }

    private Operation pickOp(DnaReader dna) {
        return dna.pickFromList(Operation.values());
    }

    private String pickDest(DnaReader dna) {
        int pos = dna.nextInt(0, memSize);
        return "m[" + pos + "]";
    }

    private String pickOneOp(DnaReader dna) {
        Optional<Boolean> isPosition = dna.next(DnaReader::nextBoolean);
        if (isPosition.isPresent()) {
            if (isPosition.get()) {
                Optional<Integer> pos = dna.next(d -> d.nextInt(0, inSize + memSize));
                return pos.map(p -> {
                    if (p < inSize) {
                        return "i[" + p + "]";
                    } else {
                        return "m[" + (p - inSize) + "]";
                    }
                }).orElse(null);

            } else {
                Optional<Float> literal = dna.next(DnaReader::nextFloat);
                return literal.map(l -> l + "f").orElse(null);
            }
        }
        return null;
    }

    public BrainAlgorithm compileBrainAlgorithm(List<Instruction> instructions) {

        String uniqueClassName = uniqueClassNameSuffix("CompiledBrain");
        String sourceCode = generateCode(uniqueClassName, instructions);
        return instantiateSourceCode(uniqueClassName, sourceCode);
    }

    private String generateCode(String uniqueClassName, List<Instruction> instructions) {
        String instructionsCode = buildInstructions(instructions);
        StringBuffer sb = new StringBuffer();

        sb.append("public class ");
        sb.append(uniqueClassName);
        sb.append(" implements com.github.kettoleon.lifesim.soup.model.creature.brain.algorithmic.BrainAlgorithm {\n\n");

        sb.append("\t public void process(float[] i, float[] m, float[] o){\n\n");
        sb.append(instructionsCode);
        sb.append("\t}\n\n");

        sb.append("\t public int getNumInstructions(){ return ");
        sb.append(instructions.size());
        sb.append(";}\n\n");

        sb.append("\t public String getAlgorithm(){ return new String(new byte[]{");
        sb.append(Arrays.stream(ArrayUtils.toObject(instructionsCode.getBytes())).map(Object::toString).collect(Collectors.joining(",")));
        sb.append("});}\n\n");

        sb.append("}");
        return sb.toString();
    }

    private String buildInstructions(List<Instruction> instructions) {
        StringBuffer sb = new StringBuffer();
        instructions.forEach(is -> sb.append("\t\t" + is.toString() + "\n"));
        return sb.toString();
    }

    public static void main(String[] args) {
        //inputs: i touched something, i'm hungry
        //outputs: move left, move right

            Creature wow = new CreatureBuilder().build(new Dna(32).getNewReader());
        System.out.println(wow.getBrain().getDescription());
    }


}
