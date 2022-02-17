package com.ick.kalambury.controllers;

import com.ick.kalambury.api.TableDto;
import com.ick.kalambury.api.TableIdDto;
import com.ick.kalambury.api.converters.TableConverter;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.exceptions.UserNotFoundException;
import com.ick.kalambury.security.AuthenticationFacade;
import com.ick.kalambury.security.Role;
import com.ick.kalambury.service.GameService;
import com.ick.kalambury.service.TableConfig;
import com.ick.kalambury.service.TableKind;
import com.ick.kalambury.storage.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/game")
public class GameControllerV1 extends GameControllerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameControllerV1.class);

    @Autowired
    public GameControllerV1(AuthenticationFacade authentication, UserDao userRepository, GameService service) {
        super(authentication, userRepository, service);
    }

    @PostMapping("/create/{uuid}/{nickname}")
    public TableIdDto createTable(@PathVariable String uuid, @PathVariable String nickname, @RequestBody TableConfig config) throws UserNotFoundException {
//        User user = userRepository.get(getPrincipal().getNickname()).orElseThrow(UserNotFoundException::new);
//        userRepository.set(user.toBuilder(null)
//                .setTableId(tableId)
//                .build());

        User user = User.newBuilder(null)
                .setUserId(uuid)
                .setNickname(nickname + "_" + uuid.split("-")[0])
                .setRole(Role.GUEST)
                .build();

        return service.createTable(user, config);
    }

    @GetMapping("/tables")
    public List<TableDto> getTables() {
        return new TableConverter().convertAll(service.getTables().stream()
                .filter(t -> t.getTableConfig().getKind() == TableKind.DEFAULT)
                .collect(Collectors.toList()));
    }

}
