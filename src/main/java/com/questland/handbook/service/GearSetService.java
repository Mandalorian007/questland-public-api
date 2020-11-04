package com.questland.handbook.service;

import com.questland.handbook.publicmodel.GearSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GearSetService {
    private final String IMAGE_SERVER = "https://questland-public-api-dot-questland-tools.uc.r.appspot.com/meta-gear-sets/";
    private final List<GearSet> gearSets = List.of(
            GearSet.builder()
                    .refCode("wind-et")
                    .title("Evernight Troubadour Wind Warrior Gear Set")
                    .build(),
            GearSet.builder()
                    .refCode("myth-gota")
                    .title("Guardian of the Afterlife Myth Warrior Gear Set")
                    .setsUsed(List.of("Myth | Guardian of the Afterlife",
                            "Noble | Lionheart Crusader",
                            "Hex | Trickster Prince",
                            "Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest"))
                    .notes(List.of("3 unlinked health collections",
                            "artifact orbs cannot link: Noble Gloves"))
                    .imageUrl(IMAGE_SERVER + "myth-gota.png")
                    .build(),
            GearSet.builder()
                    .refCode("noble-lc")
                    .title("Lionheart Crusader Noble Warrior Gear Set")
                    .setsUsed(List.of("Noble | Lionheart Crusader",
                            "Hex | Trickster Prince",
                            "Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest",
                            "Dragon | Cinderlord"))
                    .notes(List.of("2 unlinked health collections",
                            "artifact orbs cannot link: Noble Gloves, Abyss Necklace, Abyss Boots"))
                    .imageUrl(IMAGE_SERVER + "noble-lc.png")
                    .build(),
            GearSet.builder()
                    .refCode("hex-tp")
                    .title("Trickster Prince Hex Warrior Gear Set")
                    .setsUsed(List.of("Hex | Trickster Prince",
                            "Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest",
                            "Dragon | Cinderlord",
                            "Shadow | Oni Warrior"))
                    .notes(List.of("1 unlinked health collections",
                            "artifact orbs cannot link: Abyss Necklace, Abyss Boots"))
                    .imageUrl(IMAGE_SERVER + "hex-tp.png")
                    .build(),
            GearSet.builder()
                    .refCode("abyss-dc")
                    .title("Dread Captain Abyss Warrior Gear Set")
                    .setsUsed(List.of("Abyss | Dread Captain",
                            "Thunder | Knight of the Tempest",
                            "Dragon | Cinderlord",
                            "Shadow | Oni Warrior",
                            "Sacred | Triumphant Crusader"))
                    .notes(List.of("1 unlinked health collections",
                            "artifact orbs cannot link: Abyss Necklace, Abyss Boots, Dragon Talisman"))
                    .imageUrl(IMAGE_SERVER + "abyss-dc.png")
                    .build()
    );

    public List<GearSet> getGearSets() {
        return gearSets;
    }

    public Optional<GearSet> getSpecificGearSet(String refCode) {
        return gearSets.stream().filter(collection -> collection.getRefCode().equalsIgnoreCase(refCode)).findFirst();
    }
}
