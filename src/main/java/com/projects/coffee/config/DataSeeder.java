package com.projects.coffee.config;

import com.projects.coffee.entity.Bean;
import com.projects.coffee.entity.Instruction;
import com.projects.coffee.entity.Login;
import com.projects.coffee.entity.Person;
import com.projects.coffee.entity.Recipe;
import com.projects.coffee.repository.BeanRepository;
import com.projects.coffee.repository.InstructionRepository;
import com.projects.coffee.repository.LoginRepository;
import com.projects.coffee.repository.PersonRepository;
import com.projects.coffee.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Seeds a handful of public recipes on first startup so new/anonymous visitors have
 * something to browse. Skips entirely if the seed account already exists.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String SEED_USERNAME = "James Hoffmann";

    private final PersonRepository personRepository;
    private final LoginRepository loginRepository;
    private final BeanRepository beanRepository;
    private final InstructionRepository instructionRepository;
    private final RecipeRepository recipeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PersonRepository personRepository, LoginRepository loginRepository,
                       BeanRepository beanRepository, InstructionRepository instructionRepository,
                       RecipeRepository recipeRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.loginRepository = loginRepository;
        this.beanRepository = beanRepository;
        this.instructionRepository = instructionRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (loginRepository.findByUsername(SEED_USERNAME) != null) {
            return;
        }

        Person person = new Person();
        person.setFirstName("James");
        person.setLastName("Hoffmann");
        person.setUsername(SEED_USERNAME);
        person = personRepository.save(person);

        Login login = new Login();
        login.setUsername(SEED_USERNAME);
        login.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        login.setPerson(person);
        loginRepository.save(login);

        seedRecipe(person, "V60 Pour Over",
                "Bright & Fruity", "Ethiopia", "Light",
                "V60 Pour Over", 205, 500, 30, "Medium-Fine",
                "1. Rinse the V60 filter with hot water and discard the rinse water.\n" +
                        "2. Add 30g medium-fine ground coffee to the filter and level the bed.\n" +
                        "3. Bloom with 60g water (~205°F), swirl gently, and let sit for 45 seconds.\n" +
                        "4. Pour in stages up to a total of 500g water, keeping the water level steady, finishing the last pour around 2:30.\n" +
                        "5. Let the brew finish draining and swirl gently; total time should land around 3:30.",
                "Based on James Hoffmann's widely shared V60 technique (1:16.7 ratio), a solid starting point for dialing in your own taste.");

        seedRecipe(person, "Inverted AeroPress",
                "Chocolatey & Nutty", "Brazil", "Medium",
                "AeroPress (Inverted)", 212, 200, 11, "Coarse",
                "1. Assemble the AeroPress in the inverted position.\n" +
                        "2. Add 11g coarsely ground coffee to the chamber.\n" +
                        "3. Pour in 200g of just-off-boil water and stir for about 10 seconds.\n" +
                        "4. Let steep undisturbed for 2 minutes.\n" +
                        "5. Attach a rinsed filter and cap, flip onto your mug, and press slowly over about 30 seconds.",
                "Based on James Hoffmann's well-known \"ultimate\" AeroPress recipe.");

        seedRecipe(person, "Improved French Press",
                "Balanced & Sweet", "Colombia", "Medium",
                "French Press", 200, 500, 30, "Medium-Coarse",
                "1. Add 30g medium-coarse ground coffee to the press.\n" +
                        "2. Pour in 500g of hot water (just off the boil) and start the timer.\n" +
                        "3. Let steep for 4 minutes without stirring.\n" +
                        "4. Break the crust that forms on top, then skim off the foam and floating grounds with two spoons.\n" +
                        "5. Let settle for 5 more minutes, then slowly decant, leaving the sediment at the bottom undisturbed.",
                "Based on James Hoffmann's \"improved\" French press method, which skips the final full plunge to reduce sediment and bitterness.");

        seedRecipe(person, "Simple Cold Brew",
                "Smooth & Low-Acid", "Guatemala", "Medium-Dark",
                "Cold Brew", 68, 1000, 100, "Coarse",
                "1. Combine 100g coarsely ground coffee with 1000g of cold or room-temperature water in a large jar.\n" +
                        "2. Stir gently to saturate all the grounds.\n" +
                        "3. Cover and steep at room temperature or in the fridge for 12-24 hours, to taste.\n" +
                        "4. Strain through a fine mesh sieve, then filter again through a paper filter or cloth.\n" +
                        "5. Dilute the concentrate with water or milk to taste before serving over ice.",
                "Based on James Hoffmann's simple 1:10 cold brew ratio.");
    }

    private void seedRecipe(Person owner, String title,
                             String beanFlavor, String beanOrigin, String beanRoast,
                             String brewMethod, int waterTempF, int gramsOfWater, int gramsOfCoffee, String grindSize,
                             String instructionSteps, String notes) {
        Bean bean = new Bean();
        bean.setFlavor(beanFlavor);
        bean.setOrigin(beanOrigin);
        bean.setRoast(beanRoast);
        bean.setIsPublic(true);
        bean.setCreatedBy(owner.getUsername());
        bean = beanRepository.save(bean);

        Instruction instruction = new Instruction();
        instruction.setBrewMethod(brewMethod);
        instruction.setWaterTemp(waterTempF);
        instruction.setGramsOfWater(gramsOfWater);
        instruction.setGramsOfCoffee(gramsOfCoffee);
        instruction.setGrindSize(grindSize);
        instruction.setInstructionSteps(instructionSteps);
        instruction.setIsPublic(true);
        instruction.setCreatedBy(owner.getUsername());
        instruction = instructionRepository.save(instruction);

        Recipe recipe = new Recipe();
        recipe.setUser(owner);
        recipe.setBean(bean);
        recipe.setInstruction(instruction);
        recipe.setTitle(title);
        recipe.setNotes(notes);
        recipe.setIsPublic(true);
        recipeRepository.save(recipe);
    }
}
