/*
 * Copyright (C) 2014-2016 OpenKeeper
 *
 * OpenKeeper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenKeeper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenKeeper.  If not, see <http://www.gnu.org/licenses/>.
 */
package toniarts.openkeeper.game.task.worker;

import com.jme3.math.Vector2f;
import java.util.Objects;
import toniarts.openkeeper.game.controller.IMapController;
import toniarts.openkeeper.game.controller.creature.ICreatureController;
import toniarts.openkeeper.game.controller.room.AbstractRoomController.ObjectType;
import toniarts.openkeeper.game.controller.room.IRoomController;
import toniarts.openkeeper.game.navigation.INavigationService;
import toniarts.openkeeper.game.task.AbstractTileTask;
import toniarts.openkeeper.game.task.TaskType;
import toniarts.openkeeper.tools.convert.map.ArtResource;
import toniarts.openkeeper.utils.WorldUtils;
import toniarts.openkeeper.world.object.ObjectControl;

/**
 * A task for creatures to get an object from the game world
 *
 * @author Toni Helenius <helenius.toni@gmail.com>
 */
public class FetchObjectTask extends AbstractTileTask {

    private final ObjectControl object;

    public FetchObjectTask(final INavigationService navigationService, final IMapController mapController, ObjectControl object, short playerId) {
        super(navigationService, mapController, object.getObjectCoordinates().x, object.getObjectCoordinates().y, playerId);
        this.object = object;
    }

    @Override
    public Vector2f getTarget(ICreatureController creature) {
        return WorldUtils.pointToVector2f(getTaskLocation()); // FIXME 0.5f not needed?
    }

    @Override
    public boolean isValid(ICreatureController creature) {
        return object.isPickableByPlayerCreature(playerId) && !isPlayerCapacityFull();
    }

    @Override
    public int getPriority() {
        return object.getObject().getPickUpPriority();
    }

    @Override
    public String toString() {
        return "Collecting item at " + getTaskLocation();
    }

    @Override
    protected String getStringId() {
        return "546";
    }

    @Override
    public void executeTask(ICreatureController creature, float executionDuration) {
        //object.creaturePicksUp(creature);

        // TODO: perhaps chaining? everything except gold, maybe a new task class...? Fetch & deliver
    }

    @Override
    public ArtResource getTaskAnimation(ICreatureController creature) {
        return null;
    }

    @Override
    public String getTaskIcon() {
        return "Textures/GUI/moods/SJ-Claim.png";
    }

    private boolean isPlayerCapacityFull() {
        // FIXME: Object type and capacity by the object type
        for (IRoomController room : mapController.getRoomsByFunction(ObjectType.GOLD, playerId)) {
            if (!room.isFullCapacity()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isRemovable() {
        return object.getState() != ObjectControl.ObjectState.NORMAL;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.FETCH_OBJECT;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.object);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FetchObjectTask other = (FetchObjectTask) obj;
        if (!Objects.equals(this.object, other.object)) {
            return false;
        }
        return true;
    }

}
