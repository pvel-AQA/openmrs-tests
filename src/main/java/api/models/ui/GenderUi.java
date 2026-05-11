package api.models.ui;

import lombok.Getter;

import java.util.Random;

@Getter
public enum GenderUi {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    UNKNOWN("Unknown");

    private final String gender;

    GenderUi(String gender) {
        this.gender = gender;
    }

    public static GenderUi getRandomGender() {
        final Random random = new Random();
        GenderUi[] genders = values();
        return genders[random.nextInt(genders.length)];
    }
}
