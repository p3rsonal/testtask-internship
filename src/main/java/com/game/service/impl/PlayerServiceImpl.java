package com.game.service.impl;

import static com.game.repository.Utils.SpecificationHelper.filterByBanned;
import static com.game.repository.Utils.SpecificationHelper.filterByBirthday;
import static com.game.repository.Utils.SpecificationHelper.filterByExperience;
import static com.game.repository.Utils.SpecificationHelper.filterByLevel;
import static com.game.repository.Utils.SpecificationHelper.filterByName;
import static com.game.repository.Utils.SpecificationHelper.filterByProfession;
import static com.game.repository.Utils.SpecificationHelper.filterByRace;
import static com.game.repository.Utils.SpecificationHelper.filterByTitle;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.BadRequestException;
import com.game.exception.NotFoundException;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final static Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Page<Player> getPlayers(String name, String title, Race race, Profession profession, Long after, Long before,
                                   Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                   Integer maxLevel, Integer pageNumber, Integer pageSize, PlayerOrder order) {
        return playerRepository.findAll(
            getPlayerSpecification(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel),
            PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName())));
    }

    @Override
    public Integer getPlayersCount(String name, String title, Race race, Profession profession, Long after, Long before,
                                   Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                   Integer maxLevel) {
        return Math.toIntExact(playerRepository.count(
            getPlayerSpecification(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel)));
    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getId() != null) {
            log.error("Id should not be set.");
            throw new BadRequestException();
        }
        validatePlayer(player);
        enrichPlayer(player);
        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Player getPlayer(Long id) {
        validateId(id);
        return playerRepository.findById(id)
                               .orElseThrow(NotFoundException::new);
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        Player updatedPlayer = getUpdatedPlayer(id, player);
        enrichPlayer(updatedPlayer);
        return playerRepository.save(updatedPlayer);
    }

    @Override
    public Player deletePlayer(Long id) {
        Player player = getPlayer(id);
        playerRepository.delete(player);
        return player;
    }

    private Specification<Player> getPlayerSpecification(String name, String title, Race race, Profession profession, Long after,
                                                         Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                                         Integer minLevel, Integer maxLevel) {
        return Specification
            .where(filterByName(name))
            .and(filterByTitle(title))
            .and(filterByRace(race))
            .and(filterByProfession(profession))
            .and(filterByExperience(minExperience, maxExperience))
            .and(filterByLevel(minLevel, maxLevel))
            .and(filterByBirthday(after, before))
            .and(filterByBanned(banned));
    }

    private void validatePlayer(Player player) {
        if (player.getName() == null || player.getName().length() > 12 || player.getName().isEmpty()) {
            log.error(player.getName() + " - invalid characters name.");
            throw new BadRequestException();
        }
        if (player.getTitle() == null || player.getTitle().length() > 30) {
            log.error(player.getTitle() + " - invalid characters title.");
            throw new BadRequestException();
        }
        if (player.getExperience() == null || player.getExperience() < 0 || player.getExperience() > 10000000) {
            log.error(player.getExperience() + " - invalid characters experience.");
            throw new BadRequestException();
        }
        if (player.getBirthday() == null) {
            log.error("Empty birthday field.");
            throw new BadRequestException();
        }
        int year = player.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        if (year < 2000 || year > 3000) {
            log.error(year + " - invalid characters birthday year. Acceptable range: 2000...3000.");
            throw new BadRequestException();
        }
        if (player.getBanned() == null) {
            player.setBanned(Boolean.FALSE);
        }
    }

    private void enrichPlayer(Player player) {
        player.setLevel((int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
    }

    private static void validateId(Long id) {
        if (id < 1) {
            log.error(id + " - invalid id.");
            throw new BadRequestException();
        }
    }

    private Player getUpdatedPlayer(Long id, Player player) {
        Player updatedPlayer = getPlayer(id);
        if (player.getName() != null) {
            updatedPlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            updatedPlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            updatedPlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            updatedPlayer.setProfession(player.getProfession());
        }
        if (player.getBirthday() != null) {
            updatedPlayer.setBirthday(player.getBirthday());
        }
        if (player.getBanned() != null) {
            updatedPlayer.setBanned(player.getBanned());
        }
        if (player.getExperience() != null) {
            updatedPlayer.setExperience(player.getExperience());
        }
        validatePlayer(updatedPlayer);
        return updatedPlayer;
    }
}
