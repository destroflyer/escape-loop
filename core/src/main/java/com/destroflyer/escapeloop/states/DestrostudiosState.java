package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Net;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.states.models.Highscore;
import com.destroflyer.escapeloop.states.models.HighscoreDto;
import com.destroflyer.escapeloop.states.models.SetHighscoreDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class DestrostudiosState extends State {

    private static final int APP_ID = 16;
    private static final String BASE_URL = "https://destrostudios.com:8080";
    private static final String HIGHSCORE_EVALUATION = "LOWER";
    private HashMap<String, ArrayList<Highscore>> highscores = new HashMap<>();

    public void requestHighscores() {
        request(Net.HttpMethods.GET, "/apps/" + APP_ID + "/highscores", null, HighscoreDto[].class, (highscoreDtos) -> {
            highscores.clear();
            for (HighscoreDto highscoreDto : highscoreDtos) {
                highscores.computeIfAbsent(highscoreDto.getContext(), mapId -> new ArrayList<>()).add(new Highscore(highscoreDto.getUser().getLogin(), highscoreDto.getScore()));
            }
        });
    }

    public void requestSetHighscore(String mapId, long time) {
        request(
            Net.HttpMethods.POST,
            "/apps/" + APP_ID + "/setHighscore",
            SetHighscoreDto.builder()
                .context(mapId)
                .evaluation(HIGHSCORE_EVALUATION)
                .score(time)
                .build()
        );
    }

    private <Request> void request(String method, String path, Request requestBody) {
        request(method, path, requestBody, null, null);
    }

    private <Request, Response> void request(String method, String path, Request requestBody, Class<Response> responseClass, Consumer<Response> onSuccess) {
        main.getHttpState().queueRequest(
            method,
            BASE_URL + path,
            "Bearer " + main.getAuthToken(),
            requestBody,
            responseClass,
            onSuccess
        );
    }
}
