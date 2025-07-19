/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.hyMacro;

import net.minecraft.client.MinecraftClient;
import net.wurstclient.hacks.HyMacroHack;

public class HandleTeleport
{
	private final MinecraftClient MC = MinecraftClient.getInstance();
	
	public void handleTeleportLogic()
	{
		int x = MC.player.getBlockX();
		int y = MC.player.getBlockY();
		int z = MC.player.getBlockZ();
		
		if((y == 75 || y == 94) && (z <= 5 && z >= -5)
			&& ((x <= 15 && x >= 5) || (x >= -58 && x <= -45)))
		{
			HyMacroHack.now = System.currentTimeMillis();
			MC.player.networkHandler.sendChatCommand("skyblock");
			
		}else if(y == 70
			&& HyMacroHack.currentState != HyMacroHack.State.SECOND)
		{
			HyMacroHack.now = System.currentTimeMillis();
			MC.player.networkHandler.sendChatCommand("warp garden");
			
		}else if(y == 100)
		{
			HyMacroHack.now = System.currentTimeMillis();
			MC.player.networkHandler.sendChatCommand("warp garden");
			
		}else if(y == 31)
		{
			HyMacroHack.now = System.currentTimeMillis();
			MC.player.networkHandler.sendChatCommand("l skyblock");
			
		}
	}
}
