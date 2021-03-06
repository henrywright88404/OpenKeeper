/*
 * Copyright (C) 2014-2017 OpenKeeper
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
package toniarts.openkeeper.game.controller;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import toniarts.openkeeper.game.controller.object.IObjectController;
import toniarts.openkeeper.game.controller.player.PlayerSpell;

/**
 * Handles the object space
 *
 * @author Toni Helenius <helenius.toni@gmail.com>
 */
public interface IObjectsController extends IEntityWrapper<IObjectController> {

    EntityId loadObject(short objectId, short ownerId, int x, int y);

    EntityId loadObject(short objectId, short ownerId, int x, int y, float rotation);

    EntityId loadObject(short objectId, short ownerId, int x, int y, Integer money, Integer spellId);

    EntityId addRoomGold(short ownerId, int x, int y, int money, int maxMoney);

    EntityId addLooseGold(short ownerId, int x, int y, int money, int maxMoney);

    EntityId addRoomSpellBook(short ownerId, int x, int y, PlayerSpell spell);

    public EntityData getEntityData();

}
