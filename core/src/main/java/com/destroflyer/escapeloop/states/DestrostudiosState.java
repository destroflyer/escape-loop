package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.replays.json.Replay;
import com.destroflyer.escapeloop.states.models.Highscore;
import com.destroflyer.escapeloop.states.models.HighscoreDto;
import com.destroflyer.escapeloop.states.models.SetHighscoreDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import lombok.Getter;

public class DestrostudiosState extends State {

    public DestrostudiosState() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
    }
    private static final String LOG_TAG = "DESTROSTUDIOS";
    private static final int APP_ID = 16;
    private static final String BASE_URL = "https://destrostudios.com:8080";
    private static final String HIGHSCORE_EVALUATION = "LOWER";
    public static final int DISPLAYED_WORLD_RECORDS_PER_MAP = 5;
    private Json json;
    private boolean personalRecordsLoading;
    private boolean worldRecordsLoading;
    @Getter
    private HashMap<String, Highscore> personalRecords = new HashMap<>();
    @Getter
    private HashMap<String, ArrayList<Highscore>> worldRecords = new HashMap<>();

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        requestHighscores();
    }

    public void requestHighscores() {
        personalRecordsLoading = true;
        request(Net.HttpMethods.GET, "/apps/" + APP_ID + "/highscores?evaluation=" + HIGHSCORE_EVALUATION + "&login=" + main.getAccount().getLogin(), null, HighscoreDto[].class, (highscoreDtos) -> {
            personalRecords.clear();
            for (HighscoreDto highscoreDto : highscoreDtos) {
                personalRecords.put(highscoreDto.getContext(), mapHighscoreDto(highscoreDto));
            }
            personalRecordsLoading = false;
        });
        worldRecordsLoading = true;
        request(Net.HttpMethods.GET, "/apps/" + APP_ID + "/highscores?evaluation=" + HIGHSCORE_EVALUATION + "&limitPerContext=" + DISPLAYED_WORLD_RECORDS_PER_MAP, null, HighscoreDto[].class, (highscoreDtos) -> {
            worldRecords.clear();
            for (HighscoreDto highscoreDto : highscoreDtos) {
                worldRecords.computeIfAbsent(highscoreDto.getContext(), mapId -> new ArrayList<>()).add(mapHighscoreDto(highscoreDto));
            }
            worldRecordsLoading = false;
        });
    }

    private Highscore mapHighscoreDto(HighscoreDto highscoreDto) {
        Replay replay = null;
        if (highscoreDto.getMetadata() != null) {
            try {
                replay = json.fromJson(Replay.class, highscoreDto.getMetadata());
            } catch (Exception ex) {
                Gdx.app.error(LOG_TAG, "Corrupted replay - User: " + highscoreDto.getUser().getLogin() + ", Map: " + highscoreDto.getContext() + ", Replay: " + highscoreDto.getMetadata());
            }
        }
        return new Highscore(highscoreDto.getUser().getLogin(), highscoreDto.getScore(), replay);
    }

    public void requestSetHighscore(String mapId, int totalFrames, Replay replay) {
        request(
            Net.HttpMethods.POST,
            "/apps/" + APP_ID + "/setHighscore",
            SetHighscoreDto.builder()
                .context(mapId)
                .evaluation(HIGHSCORE_EVALUATION)
                .score(totalFrames)
                .metadata(json.toJson(replay))
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
