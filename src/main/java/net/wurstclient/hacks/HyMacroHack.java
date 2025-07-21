/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.SearchTags;
import net.wurstclient.WurstClient;
import net.wurstclient.hacks.hyMacro.*;
import net.wurstclient.mixinterface.IKeyBinding;

@SearchTags({"macro"})
public final class HyMacroHack extends Hack implements UpdateListener
{
	
	public enum State
	{
		FIRST,
		SECOND,
		THIRD,
		FOURTH,
		FIFTH
	}
	
	public static long RANDOM = (long)(Math.random() * 660L);
	private static final long LADDER_SEC = 4_000L + RANDOM;
	private static final long PEST_SEC = 60_000L + RANDOM;
	public static int TickCounter = 0;
	public static long now;
	public static long elapsed;
	public static long stateStart;
	
	public static State currentState;
	
	public HyMacroHack()
	{
		super("HyMacroHack");
		setCategory(Category.MOVEMENT);
	}
	
	private HandleTeleport handleTeleport;
	private AutoReconnectHack autoReconnectHack;
	private FightBotHack fightBotHack;
	private ActualMacro actualMacro;
	private MovementHack movementHack;
	
	@Override
	protected void onEnable()
	{
		fightBotHack = WurstClient.INSTANCE.getHax().fightBotHack;
		autoReconnectHack = WurstClient.INSTANCE.getHax().autoReconnectHack;
		movementHack = WurstClient.INSTANCE.getHax().movementHack;
		
		actualMacro = new ActualMacro();
		handleTeleport = new HandleTeleport();
		
		currentState = State.FIRST;
		autoReconnectHack.setEnabled(true);
		stateStart = System.currentTimeMillis();
		Utility.Notify("[hy] - Started...");
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		autoReconnectHack.setEnabled(false);
		fightBotHack.setEnabled(false);
		movementHack.setEnabled(false);
		Utility.ResetKeys();
		Utility.Notify("[hy] - Stopped...");
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		
		if(MC.player == null)
			return;
		TickCounter++;
		if(TickCounter % 40 != 0)
			return;
		now = System.currentTimeMillis();
		elapsed = now - stateStart;
		// ACTUAL MACRO
		
		IKeyBinding.get(MC.options.sprintKey).setPressed(true);
		handleTeleport.handleTeleportLogic();
		
		switch(currentState)
		{
			case FIRST:
			{
				actualMacro.WarpHack();
				
				currentState = State.SECOND;
				stateStart = now;
				Utility.Notify("[hy] - Climbing Ladder...");
			}
			break;
			
			case SECOND:
			{
				actualMacro.LadderHack();
				if(elapsed >= LADDER_SEC)
				{
					currentState = State.THIRD;
					stateStart = now;
					Utility.Notify("[hy] - Vacumming Pests...");
					Utility.SwapHotbarSlotsVacuum();
				}
			}
			break;
			
			case THIRD:
			{
				fightBotHack.setEnabled(true);
				movementHack.setEnabled(true);
				IKeyBinding.get(MC.options.backKey).resetPressedState();
				if(elapsed >= PEST_SEC)
				{
					fightBotHack.setEnabled(false);
					movementHack.setEnabled(false);
					currentState = State.FOURTH;
					stateStart = now;
					Utility.Notify("[hy] - Warping...");
				}
			}
			break;
			
			case FOURTH:
			{
				actualMacro.WarpHack();
				
				currentState = State.FIFTH;
				stateStart = now;
				Utility.Notify("[hy] - Farming...");
			}
			break;
			
			case FIFTH:
			{
				Utility.SwapHotbarSlotsDicer();
				actualMacro.FarmingHack();
			}
			break;
			
		}
		// pesthack
		// movement hack
		// FARMING
		// fightbot
		
	}
}
