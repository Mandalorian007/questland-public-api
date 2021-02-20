package com.questland.handbook.service;

import com.questland.handbook.publicmodel.OptimizedGearTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OptimizedGearTemplateService {
    private final String IMAGE_SERVER = "https://questland-public-api-dot-questland-tools.uc.r.appspot.com/meta-gear-templates/";
    private final List<OptimizedGearTemplate> optimizedGearTemplates = List.of(
            OptimizedGearTemplate.builder()
                    .refCode("beast-bb")
                    .title("Beast Brawler - Beast Warrior Gear Set")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("ice-st")
                    .title("Sabertooth - Ice Warrior Gear Set")
                    .setsUsed(List.of("Ice | Sabertooth",
                            "Venom | Poison Master",
                            "Wind | Evernight Troubadour",
                            "Myth | Guardian of the Afterlife",
                            "Noble | Lionheart Crusader"))
                    .notes(List.of("1 unlinked health collections",
                            "All artifact orbs linked"))
                    .imageUrl(IMAGE_SERVER + "ice-st.png")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("venom-pm")
                    .title("Poison Master - Venom Warrior Gear Template")
                    .setsUsed(List.of("Venom | Poison Master",
                            "Wind | Evernight Troubadour",
                            "Myth | Guardian of the Afterlife",
                            "Noble | Lionheart Crusader",
                            "Hex | Trickster Prince"))
                    .notes(List.of("2 unlinked health collections",
                            "All artifact orbs linked"))
                    .imageUrl(IMAGE_SERVER + "venom-pm.png")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("wind-et")
                    .title("Evernight Troubadour - Wind Warrior Gear Template")
                    .setsUsed(List.of("Wind | Evernight Troubadour",
                            "Myth | Guardian of the Afterlife",
                            "Noble | Lionheart Crusader",
                            "Hex | Trickster Prince",
                            "Abyss | Dread Captain"))
                    .notes(List.of("3 unlinked health collections",
                            "Artifact orbs cannot link the following gear: Noble Gloves"))
                    .imageUrl(IMAGE_SERVER + "wind-et.png")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("myth-gota")
                    .title("Guardian of the Afterlife - Myth Warrior Gear Template")
                    .setsUsed(List.of("Myth | Guardian of the Afterlife",
                            "Noble | Lionheart Crusader",
                            "Hex | Trickster Prince",
                            "Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest"))
                    .notes(List.of("3 unlinked health collections",
                            "Artifact orbs cannot link the following gear: Noble Gloves"))
                    .imageUrl(IMAGE_SERVER + "myth-gota.png")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("noble-lc")
                    .title("Lionheart Crusader - Noble Warrior Gear Template")
                    .setsUsed(List.of("Noble | Lionheart Crusader",
                            "Hex | Trickster Prince",
                            "Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest",
                            "Dragon | Cinderlord"))
                    .notes(List.of("2 unlinked health collections",
                            "Artifact orbs cannot link: Noble Gloves, Abyss Necklace, Abyss Boots"))
                    .imageUrl(IMAGE_SERVER + "noble-lc.png")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("hex-tp")
                    .title("Trickster Prince - Hex Warrior Gear Template")
                    .setsUsed(List.of("Hex | Trickster Prince",
                            "Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest",
                            "Dragon | Cinderlord",
                            "Shadow | Oni Warrior"))
                    .notes(List.of("1 unlinked health collections",
                            "Artifact orbs cannot link: Abyss Necklace, Abyss Boots"))
                    .imageUrl(IMAGE_SERVER + "hex-tp.png")
                    .build(),
            OptimizedGearTemplate.builder()
                    .refCode("abyss-dc")
                    .title("Dread Captain - Abyss Warrior Gear Template")
                    .setsUsed(List.of("Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest",
                            "Dragon | Cinderlord",
                            "Shadow | Oni Warrior",
                            "Sacred | Triumphant Crusader"))
                    .notes(List.of("1 unlinked health collections",
                            "Artifact orbs cannot link: Abyss Necklace, Abyss Boots, Dragon Talisman"))
                    .imageUrl(IMAGE_SERVER + "abyss-dc.png")
                    .build()
    );

    public List<OptimizedGearTemplate> getOptimizedGearTemplates() {
        return optimizedGearTemplates;
    }

    public Optional<OptimizedGearTemplate> getOptimizedGearTemplate(String refCode) {
        return optimizedGearTemplates.stream().filter(collection -> collection.getRefCode().equalsIgnoreCase(refCode)).findFirst();
    }
}
