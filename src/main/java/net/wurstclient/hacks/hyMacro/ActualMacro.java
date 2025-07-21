/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.hyMacro;

import net.minecraft.client.MinecraftClient;
import net.wurstclient.mixinterface.IKeyBinding;

public class ActualMacro
{
	
	private final static MinecraftClient MC = MinecraftClient.getInstance();
	
	public void WarpHack()
	{
		MC.player.networkHandler.sendChatCommand("warp garden");
	}
	
	// PESTHACK ^^
	
	public void FarmingHack()
	{
		int x = MC.player.getBlockX();
		int z = MC.player.getBlockZ();
		
		IKeyBinding.get(MC.options.attackKey).setPressed(true);
		
		if(x == 140 && z == -48)
		{
			MC.player.networkHandler.sendChatCommand("warp garden");
		}
		
		if((z >= -48 && z <= 143) && (x == 49 || x == 63 || x == 77 || x == 91
			|| x == 105 || x == 119 || x == 133))
		{
			IKeyBinding.get(MC.options.attackKey).setPressed(true);
			IKeyBinding.get(MC.options.rightKey).setPressed(true);
			IKeyBinding.get(MC.options.forwardKey).setPressed(true);
			IKeyBinding.get(MC.options.leftKey).resetPressedState();
			
		}else if((z >= -48 && z <= 143) && (x == 56 || x == 70 || x == 84
			|| x == 98 || x == 112 || x == 126 || x == 140))
		{
			IKeyBinding.get(MC.options.attackKey).setPressed(true);
			IKeyBinding.get(MC.options.leftKey).setPressed(true);
			IKeyBinding.get(MC.options.forwardKey).setPressed(true);
			IKeyBinding.get(MC.options.rightKey).resetPressedState();
			
		}else
		{
			IKeyBinding.get(MC.options.attackKey).setPressed(true);
			IKeyBinding.get(MC.options.forwardKey).setPressed(true);
			IKeyBinding.get(MC.options.leftKey).resetPressedState();
			IKeyBinding.get(MC.options.rightKey).resetPressedState();
			
		}
	}
	
	// FARMINGHACK ^^
	
	public void LadderHack()
	{
		IKeyBinding.get(MC.options.backKey).setPressed(true);
	}
	
	// LADDERHACK ^^
}
