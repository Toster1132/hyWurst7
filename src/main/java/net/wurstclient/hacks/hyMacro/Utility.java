/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.hyMacro;

import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.wurstclient.mixinterface.IKeyBinding;
import net.minecraft.screen.slot.SlotActionType;

public class Utility
{
	private final static MinecraftClient MC = MinecraftClient.getInstance();
	private static final int HOTBAR_OFFSET = 36;
	
	public static void Notify(String message)
	{
		MC.inGameHud.getChatHud().addMessage(
			Text.literal(message).formatted(Formatting.RED, Formatting.BOLD));
	}
	
	public static void SwapHotbarSlotsVacuum()
	{
		SwapHotbarSlots(0, 1, Set.of(Items.MINECART, Items.TNT_MINECART,
			Items.CHEST_MINECART, Items.COMPASS));
	}
	
	public static void SwapHotbarSlotsDicer()
	{
		SwapHotbarSlots(0, 1, Set.of(Items.GOLDEN_AXE, Items.COMPASS));
	}
	
	public static void ResetKeys()
	{
		IKeyBinding.get(MC.options.attackKey).resetPressedState();
		IKeyBinding.get(MC.options.useKey).resetPressedState();
		IKeyBinding.get(MC.options.forwardKey).resetPressedState();
		IKeyBinding.get(MC.options.backKey).resetPressedState();
		IKeyBinding.get(MC.options.rightKey).resetPressedState();
		IKeyBinding.get(MC.options.leftKey).resetPressedState();
	}
	
	private static void SwapHotbarSlots(int slot1, int slot2,
		Set<Item> blockedItems)
	{
		
		if(MC.player == null || MC.interactionManager == null)
			return;
		if(slot1 < 0 || slot1 > 8 || slot2 < 0 || slot2 > 8)
			return;
		
		ItemStack handStack = MC.player.getMainHandStack();
		Item handItem = handStack.getItem();
		boolean handEmpty = handStack.isEmpty();
		boolean allowedItem = !blockedItems.contains(handItem);
		
		if(allowedItem || handEmpty)
		{
			
			if(!MC.player.currentScreenHandler.getCursorStack().isEmpty())
			{
				return;
			}
			
			if(MC.player.getInventory().getStack(slot1).isEmpty()
				|| MC.player.getInventory().getStack(slot2).isEmpty())
			{
				return;
			}
			
			int invSlot1 = HOTBAR_OFFSET + slot1;
			int syncId = MC.player.currentScreenHandler.syncId;
			
			MC.interactionManager.clickSlot(syncId, invSlot1, slot2,
				SlotActionType.SWAP, MC.player);
			
		}
	}
	
}
