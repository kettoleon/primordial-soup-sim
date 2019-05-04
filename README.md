# Primordial soup simulator

This is just my naive attempt at making neural network and genetic algorithms on my own, without having read much about
 the topic.
 
The objective is to learn from experience by solving the problems I find on the way. As the complexity of the exercise
 increases, I will need to learn/read a more about the subject and also start using other already available libraries.  

The simulation basically generates a genetically random initial population and puts them in the world with randomly placed food.
Every time a creature eats one PlantParticle, another piece of food spawns randomly.
The creature that has the highest fitness mates with the other 19 best creatures to generate the next generation offspring.
Next's offspring gene pool is based on the best survivors of all time, not just from the latest generation. 

> NOTE: I later learned this could be seen as a type of "elitist evolution technique"

There is also some healthy radiation to induce random mutation to the offspring.

![screenshot](screenshot.png "Sample screenshot of an early stage")

After each generation, a `prev.sd` file is stored so that the simulation can be killed and automatically resumes from that generation.
If you wish to start clean, just remove the file.

The Brain does not work like a neural network ATM. I decided to make the experiment to create brains out of random java
 code, and it works to just get started.
The idea is to implement a proper neural network at some point in this exercise ^.^"...

> NOTE: This turned out to be a more valid approach than I anticipated. This is called Genetic Programming and there is
> a lot of research on the subject.

There is an initial implementation of a simple neural network based brain using Encog library. The genes express the
neural network's expected behaviour instead of the weights themselves. 
However It does not look like it is giving better results than the Genetic Programming approach. I'll keep trying to 
make it work though.

If you run the simulation (with the current settings - plenty of food) you should be able to observe how the creatures
tend to evolve towards just moving in circles, faster and faster the more hungry they are. 
However, they soon learn that the sense of smell is very useful and start using it. You will see how they end up
"chasing" their food until they can't find any around and die miserably by trying to find more food standing still 
and only rotating...

If you want to run a faster simulation you can try to change the setting in `SimulationRenderingSystem` to render 
every 5, 10 or 20 ticks. If you want a smooth representation (but slower sim) you can stop the simulation and change 
this code to draw every tick.

# Overview of the code

Keep in mind I wanted to quickly prototype in my spare time, so the code is still a little bit disorganised, 
you will find a lot of code that does not follow even the basic principles of clean code ^.^"...

The main class is PrimordialSoupSimulator. The simulation consists of different Systems that are run one after another
 for each simulation step/tick.

## Systems

There are (for now) the following systems:
    
    - WorldSystem: Should run updates on the world state (Currently not enabled yet)
    - VegetationSystem: Runs updates on the food for the creatures
    - CreatureSystem: Runs updates on the creatures
    - SimulationRenderingSystem: Renders the simulation every few ticks
    
## Entities/Model

    - Creature: Has a Genome, a Brain, inputs, outputs, dies, counts how many plants has eaten...
    - PlantParticle: Is an inert plant that can be eaten by a Creature

## How to change simulation parameters

There is currently no UI to set up simulation parameters, so until we make one, here is a list of parameters you can tweak:

    - CreatureSystem
        - INITIAL_POPULATION: How many creatures to spawn at each iteration
        - INITIAL_GENES: Number of genes of the initial population
        - RADIATION: Chance of a given gene to mutate (defaults to 0.1f)
    - VegetationSystem
        - INITIAL_FOOD: How many food particles are there in the map at any given time
    - World
        - GRID_SIZE: Non-functional setting to tune performance of the food-seeking algorithms. The world stores PlantParticles into a hashmap of grid sections for faster iteration by zones.
    - SimulationRenderingSystem
        - DRAW_EVERY_X_TICKS: Non-functional setting to tell how often we should render the simulation. You can tweak this to have faster simulations in expense to less smooth animation.
        