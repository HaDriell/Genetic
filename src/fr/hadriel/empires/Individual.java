package fr.hadriel.empires;

import java.util.Objects;

public class Individual {
    public final Tribe tribe;

    public Individual(Tribe tribe) {
        this.tribe = Objects.requireNonNull(tribe);

    }
}
