/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.hyMacro;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.SlotActionType;
import net.wurstclient.mixinterface.IKeyBinding;

public class ActualMacro
{
	
	private final static MinecraftClient MC = MinecraftClient.getInstance();
	
	public void WarpG()
	{
		MC.player.networkHandler.sendChatCommand("warp garden");
	}
	
	public void WarpHub()
	{
		MC.player.networkHandler.sendChatCommand("hub");
	}
	
	// WARP ^^
	
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
	
	private boolean hasOpenedStorage = false;
	private boolean overclicked = false;
	private int slot = 0;
	private long start;
	private long MOVEMENT = 1_000L;
	private boolean hasWarped = false;
	private static long StashNow;
	private static long StashElapsed;
	
	public void StashHack()
	{
		StashNow = System.currentTimeMillis();
		StashElapsed = StashNow - start;
		
		if(!hasWarped)
		{
			MC.player.setYaw(263);
			MC.player.setPitch(0);
			
			if(StashElapsed >= 500L)
			{
				IKeyBinding.get(MC.options.forwardKey).setPressed(true);
				hasWarped = true;
				start = StashNow;
			}
			
			return;
		}
		
		if(!hasOpenedStorage)
		{
			if(StashElapsed >= MOVEMENT)
			{
				IKeyBinding.get(MC.options.forwardKey).setPressed(false);
				IKeyBinding.get(MC.options.useKey).setPressed(true);
				hasOpenedStorage = true;
				start = StashNow;
			}
			
			return;
		}
		IKeyBinding.get(MC.options.useKey).resetPressedState();
		if(StashElapsed < 500L || MC.player.currentScreenHandler == null)
			return;
		
		int syncId = MC.player.currentScreenHandler.syncId;
		int totalSlots = MC.player.currentScreenHandler.slots.size();
		int chestSlotCount = totalSlots - 36;
		int playerInvStart = chestSlotCount;
		int hotbarStart = totalSlots - 9;
		
		if(!overclicked)
		{
			int currentSlot = playerInvStart + slot;
			if(currentSlot < hotbarStart)
			{
				MC.interactionManager.clickSlot(syncId, currentSlot, 0,
					SlotActionType.QUICK_MOVE, MC.player);
				slot++;
				start = StashNow;
			}else
			{
				overclicked = true;
			}
		}
	}
	
	// STASHHACK ^^
}
