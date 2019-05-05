package com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v1;

import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.SimpleGeneticProgrammingCreatureBuilder;
import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import com.github.kettoleon.primordial.soup.model.creature.brain.NoBrain;
import com.github.kettoleon.primordial.soup.model.genetics.ChromosomeBasedBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.GeneReader;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;
import com.github.kettoleon.primordial.soup.util.JavaCompilerUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class AlgorithmicBrainBuilder implements ChromosomeBasedBuilder<Brain> {


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
    public Brain build(GeneReader reader) {
        if (reader.hasMoreGenes()) {
            memSize = reader.nextInt(0, 16);
            BrainAlgorithm brainAlgorithm = buildBrainAlgorithm(reader);
            return new AlgorithmicBrain(new float[memSize], brainAlgorithm);
        }
        return new NoBrain();
    }

    private BrainAlgorithm buildBrainAlgorithm(GeneReader dna) {

        List<Instruction> instructions = new ArrayList<>();
        Set<String> usedMemPositions = new HashSet<>();

        for (int i = 0; i < inSize; i++) {
            Optional<String> dest = dna.next(this::pickMemDest);
            Optional<Operation> op = dna.next(this::pickOp);
            String op1 = "i[" + i + "]";
            String op2 = dna.next(GeneReader::nextFloat).map(f -> f + "f").orElse("0f");
            if (dest.isPresent() && op.isPresent()) {
                usedMemPositions.add(dest.get());
                instructions.add(new Instruction(dest.get(), op.get(), op1, op2));
            }
        }


        while (dna.remainingGenes() > 7 * outSize) {
            Optional<String> dest = dna.next(this::pickMemDest);
            Optional<Operation> op = dna.next(this::pickOp);
            Optional<String[]> ops = dna.next(d -> pickMemOrLiteral(d, usedMemPositions));
            if (dest.isPresent() && op.isPresent() && ops.isPresent()) {
                usedMemPositions.add(dest.get());
                instructions.add(new Instruction(dest.get(), op.get(), ops.get()));
            }
        }


        for (int i = 0; i < outSize; i++) {
            String dest = "o[" + i + "]";
            Optional<Operation> op = dna.next(this::pickOp);
            Optional<String[]> ops = dna.next(d -> pickMemOrLiteral(d, usedMemPositions));
            if (op.isPresent() && ops.isPresent()) {
                instructions.add(new Instruction(dest, op.get(), ops.get()));
            }
        }

        reduce(instructions);

        return compileBrainAlgorithm(instructions);
    }

    private String[] pickMemOrLiteral(GeneReader dna, Set<String> usedMemPositions) {

        Optional<String> op1 = dna.next(d -> this.pickOneOp(d, usedMemPositions));
        Optional<String> op2 = dna.next(d -> this.pickOneOp(d, usedMemPositions));

        if (op1.isPresent() && op2.isPresent()) {
            return new String[]{op1.get(), op2.get()};
        }
        return null;

    }

    private void reduce(List<Instruction> instructions) {
        Collections.reverse(instructions);
        List<Instruction> toRemove = new ArrayList<>();
        toRemove.addAll(instructions); //All guilty unless proven differently

        List<Instruction> outputAssignements = findOutputAssignements(instructions);
        toRemove.removeAll(outputAssignements); //Save output assignements at least

        for (Instruction instruction : outputAssignements) {
            saveDependencies(instructions, toRemove, instruction);
        }

        instructions.removeAll(toRemove);
        Collections.reverse(instructions);
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


    private Operation pickOp(GeneReader dna) {
        return dna.pickFromList(Operation.values());
    }

    private String pickMemDest(GeneReader dna) {
        int pos = dna.nextInt(0, memSize);
        return "m[" + pos + "]";
    }

    private String pickOneOp(GeneReader dna, Set<String> mempos) {
        if (dna.remainingGenes() > 2) {
            boolean isPosition = dna.nextBoolean();
            if (isPosition) {
                return dna.pickFromList(mempos);
            } else {
                Optional<Float> literal = dna.next(GeneReader::nextFloat);
                return literal.map(l -> l + "f").orElse(null);
            }
        }
        return null;
    }

    public BrainAlgorithm compileBrainAlgorithm(List<Instruction> instructions) {

        String uniqueClassName = JavaCompilerUtils.uniqueClassNameSuffix("CompiledBrain");
        String sourceCode = generateCode(uniqueClassName, instructions);
        return JavaCompilerUtils.instantiateSourceCode(uniqueClassName, sourceCode);
    }

    private String generateCode(String uniqueClassName, List<Instruction> instructions) {
        String instructionsCode = buildInstructions(instructions);
        StringBuffer sb = new StringBuffer();

        sb.append("public class ");
        sb.append(uniqueClassName);
        sb.append(" implements com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v1.BrainAlgorithm {\n\n");

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

        Creature wow = new SimpleGeneticProgrammingCreatureBuilder().build(new Genome(32));
        System.out.println(wow.getBrain().getDescription());
    }


}
