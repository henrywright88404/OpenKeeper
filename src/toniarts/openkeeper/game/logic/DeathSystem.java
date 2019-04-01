/*
 * Copyright (C) 2014-2018 OpenKeeper
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
package toniarts.openkeeper.game.logic;

import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.awt.Point;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import toniarts.openkeeper.game.component.Death;
import toniarts.openkeeper.game.component.Gold;
import toniarts.openkeeper.game.component.Owner;
import toniarts.openkeeper.game.component.Position;
import toniarts.openkeeper.game.controller.IObjectsController;
import toniarts.openkeeper.tools.convert.map.Variable;
import toniarts.openkeeper.utils.WorldUtils;

/**
 * The waste disposal class, removes entities after reasonable amount of time
 * has passed (i.e. death animations or corpse decays have passed)
 *
 * @author Toni Helenius <helenius.toni@gmail.com>
 */
public class DeathSystem implements IGameLogicUpdatable {

    private final IObjectsController objectsController;
    private final EntitySet deathEntities;
    private final EntityData entityData;
    private final SafeArrayList<EntityId> entityIds;
    private final int timeToDecay;

    public DeathSystem(EntityData entityData, Map<Variable.MiscVariable.MiscType, Variable.MiscVariable> gameSettings,
            IObjectsController objectsController) {
        this.objectsController = objectsController;
        this.entityData = entityData;
        entityIds = new SafeArrayList<>(EntityId.class);

        timeToDecay = (int) gameSettings.get(Variable.MiscVariable.MiscType.DEAD_BODY_DIES_AFTER_SECONDS).getValue();

        deathEntities = entityData.getEntities(Death.class);
        processAddedEntities(deathEntities);
    }

    @Override
    public void processTick(float tpf, double gameTime) {
        if (deathEntities.applyChanges()) {

            processAddedEntities(deathEntities.getAddedEntities());

            processDeletedEntities(deathEntities.getRemovedEntities());
        }

        // Decay stuff
        for (EntityId entityId : entityIds.getArray()) {
            Death death = entityData.getComponent(entityId, Death.class);
            if (gameTime - death.startTime >= timeToDecay) {
                entityData.removeEntity(entityId);
            }
        }
    }


    private void processAddedEntities(Set<Entity> entities) {
        for (Entity entity : entities) {
            int index = Collections.binarySearch(entityIds, entity.getId());
            entityIds.add(~index, entity.getId());
            handleLootDrop(entity.getId());
        }
    }

    private void processDeletedEntities(Set<Entity> entities) {
        for (Entity entity : entities) {
            int index = Collections.binarySearch(entityIds, entity.getId());
            entityIds.remove(index);
        }
    }

    private void handleLootDrop(EntityId entityId) {

        // Drop gold
        Gold gold = entityData.getComponent(entityId, Gold.class);
        if (gold != null && gold.gold > 0) {
            Position position = entityData.getComponent(entityId, Position.class);
            Owner owner = entityData.getComponent(entityId, Owner.class);
            Point point = WorldUtils.vectorToPoint(position.position);
            // TODO: some central place, we need to add more than one pile if it exceeds the max
            objectsController.addLooseGold(owner.ownerId, point.x, point.y, gold.gold, gold.maxGold);
            entityData.removeComponent(entityId, Gold.class);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        deathEntities.release();
        entityIds.clear();
    }

}