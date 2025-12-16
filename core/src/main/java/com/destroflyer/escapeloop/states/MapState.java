package com.destroflyer.escapeloop.states;

import com.destroflyer.escapeloop.Main;
import com.destroflyer.escapeloop.game.Cinematic;
import com.destroflyer.escapeloop.game.Map;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.util.MapImport;

import lombok.Getter;

public abstract class MapState<MapType extends Map, MapIngameState extends State> extends State {

    public MapState(int mapIndex) {
        if (MapImport.isSrcMapsDirectoryPathSet()) {
            MapImport.importMap(mapIndex);
        }
        this.mapIndex = mapIndex;
    }
    protected int mapIndex;
    @Getter
    protected MapType map;
    @Getter
    private MapRenderState mapRenderState;
    private MapIngameState mapIngameState;
    private MapPauseState mapPauseState;

    @Override
    public void create() {
        super.create();
        mapRenderState = new MapRenderState(this);
        mapIngameState = createMapIngameState();
        mapPauseState = new MapPauseState(this);
        childStates.add(mapRenderState);
        childStates.add(mapIngameState);
        childStates.add(mapPauseState);
        map = createMap();
        map.initialize();
    }

    protected abstract MapIngameState createMapIngameState();

    protected abstract MapType createMap();

    @Override
    public void onAdd(Main main) {
        super.onAdd(main);
        main.addState(mapRenderState);
        main.addState(mapIngameState);
    }

    public void openPauseMenu() {
        Cinematic cinematic = map.getCinematic();
        if (cinematic != null) {
            cinematic.onPauseMenuOpen();
        }
        main.removeState(mapIngameState);
        main.addState(mapPauseState);
    }

    public void closePauseMenu() {
        Cinematic cinematic = map.getCinematic();
        if (cinematic != null) {
            cinematic.onPauseMenuClose();
        }
        main.removeState(mapPauseState);
        main.addState(mapIngameState);
    }

    public void backToMapSelection() {
        Cinematic cinematic = map.getCinematic();
        if (cinematic != null) {
            cinematic.finish();
        }
        switchToState(main.getMapSelectionState());
    }
}
