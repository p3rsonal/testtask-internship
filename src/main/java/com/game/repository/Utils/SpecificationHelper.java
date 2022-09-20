package com.game.repository.Utils;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import java.util.Date;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationHelper {

    public static Specification<Player> filterByName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Player> filterByTitle(String title) {
        return (root, query, cb) -> title == null ? null : cb.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Player> filterByRace(Race race) {
        return (root, query, cb) -> race == null ? null : cb.equal(root.get("race"), race);
    }

    public static Specification<Player> filterByProfession(Profession profession) {
        return (root, query, cb) -> profession == null ? null : cb.equal(root.get("profession"), profession);
    }

    public static Specification<Player> filterByExperience(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("experience"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("experience"), min);
            return cb.between(root.get("experience"), min, max);
        };
    }

    public static Specification<Player> filterByLevel(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("level"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("level"), min);
            return cb.between(root.get("level"), min, max);
        };
    }

    public static Specification<Player> filterByBirthday(Long after, Long before) {
        return (root, query, cb) -> {
            if (after == null && before == null) return null;
            if (after == null) return cb.lessThanOrEqualTo(root.get("birthday"), new Date(before));
            if (before == null) return cb.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
            return cb.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    public static Specification<Player> filterByBanned(Boolean isBanned) {
        return (root, query, cb) -> {
            if (isBanned == null) return null;
            if (isBanned) return cb.isTrue(root.get("banned"));
            return cb.isFalse(root.get("banned"));
        };
    }

}
