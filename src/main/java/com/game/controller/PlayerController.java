package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {

    private final PlayerService playerService;
    private final static Logger log = LoggerFactory.getLogger(PlayerController.class);

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/rest/players")
    public List<Player> getPlayers(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                   @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                   @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                   @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order) {
        log.info("GET /rest/players");

        return playerService
            .getPlayers(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, pageNumber, pageSize, order)
            .getContent();
    }

    @GetMapping("/rest/players/count")
    public Integer getPlayersCount(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        log.info("GET /rest/players/count");
        return playerService.getPlayersCount(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
    }

    @PostMapping("/rest/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        log.info("POST /rest/players");
        return ResponseEntity.ok(playerService.createPlayer(player));
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) {
        log.info("GET /rest/players/" + id);
        return ResponseEntity.ok(playerService.getPlayer(id));
    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody Player player,
                                               @PathVariable Long id) {
        log.info("POST /rest/players/" + id);
        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id) {
        log.info("DELETE /rest/players/" + id);
        return ResponseEntity.ok(playerService.deletePlayer(id));
    }
}
