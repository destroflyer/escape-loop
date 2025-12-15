package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Net;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.states.models.Highscore;
import com.destroflyer.escapeloop.states.models.HighscoreDto;
import com.destroflyer.escapeloop.states.models.SetHighscoreDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import lombok.Getter;

public class DestrostudiosState extends State {

    private static final int APP_ID = 16;
    private static final String BASE_URL = "https://destrostudios.com:8080";
    private static final String HIGHSCORE_EVALUATION = "LOWER";
    public static final int DISPLAYED_WORLD_RECORDS_PER_MAP = 5;
    private boolean personalRecordsLoading;
    private boolean worldRecordsLoading;
    @Getter
    private HashMap<String, Highscore> personalRecords = new HashMap<>();
    @Getter
    private HashMap<String, ArrayList<Highscore>> worldRecords = new HashMap<>();

    public void requestHighscores() {
        personalRecordsLoading = true;
        request(Net.HttpMethods.GET, "/apps/" + APP_ID + "/highscores?evaluation=" + HIGHSCORE_EVALUATION + "&login=" + main.getAccount().getLogin(), null, HighscoreDto[].class, (highscoreDtos) -> {
            personalRecords.clear();
            for (HighscoreDto highscoreDto : highscoreDtos) {
                personalRecords.put(highscoreDto.getContext(), new Highscore(highscoreDto.getUser().getLogin(), highscoreDto.getScore()));
            }
            personalRecordsLoading = false;
        });
        worldRecordsLoading = true;
        request(Net.HttpMethods.GET, "/apps/" + APP_ID + "/highscores?evaluation=" + HIGHSCORE_EVALUATION + "&limitPerContext=" + DISPLAYED_WORLD_RECORDS_PER_MAP, null, HighscoreDto[].class, (highscoreDtos) -> {
            worldRecords.clear();
            for (HighscoreDto highscoreDto : highscoreDtos) {
                worldRecords.computeIfAbsent(highscoreDto.getContext(), mapId -> new ArrayList<>()).add(new Highscore(highscoreDto.getUser().getLogin(), highscoreDto.getScore()));
            }
            worldRecordsLoading = false;
        });
    }

    public void requestSetHighscore(String mapId, int totalFrames) {
        request(
            Net.HttpMethods.POST,
            "/apps/" + APP_ID + "/setHighscore",
            SetHighscoreDto.builder()
                .context(mapId)
                .evaluation(HIGHSCORE_EVALUATION)
                .score(totalFrames)
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

    public boolean isLoading() {
        return personalRecordsLoading || worldRecordsLoading;
    }
}
