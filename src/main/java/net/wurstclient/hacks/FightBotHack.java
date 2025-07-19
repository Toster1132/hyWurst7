/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathPos;
import net.wurstclient.ai.PathProcessor;
import net.wurstclient.commands.PathCmd;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.DontSaveState;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.AttackSpeedSliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.PauseAttackOnContainersSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.SwingHandSetting;
import net.wurstclient.settings.SwingHandSetting.SwingHand;
import net.wurstclient.settings.filterlists.EntityFilterList;
import net.wurstclient.util.EntityUtils;

@SearchTags({"fight bot"})
@DontSaveState
public final class FightBotHack extends Hack
	implements UpdateListener, RenderListener
{
	private final SliderSetting range = new SliderSetting("Range",
		"Attack range (like Killaura)", 4.25, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final AttackSpeedSliderSetting speed =
		new AttackSpeedSliderSetting();
	
	private final SwingHandSetting swingHand = new SwingHandSetting(
		SwingHandSetting.genericCombatDescription(this), SwingHand.CLIENT);
	
	private final SliderSetting distance = new SliderSetting("Distance",
		"How closely to follow the target.\n"
			+ "This should be set to a lower value than Range.",
		3, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final CheckboxSetting useAi =
		new CheckboxSetting("Use AI (experimental)", false);
	
	private final PauseAttackOnContainersSetting pauseOnContainers =
		new PauseAttackOnContainersSetting(true);
	
	private final EntityFilterList entityFilters =
		EntityFilterList.genericCombat();
	
	private EntityPathFinder pathFinder;
	private PathProcessor processor;
	private int ticksProcessing;
	
	public FightBotHack()
	{
		super("FightBot");
		
		setCategory(Category.COMBAT);
		addSetting(range);
		addSetting(speed);
		addSetting(swingHand);
		addSetting(distance);
		addSetting(useAi);
		addSetting(pauseOnContainers);
		
		entityFilters.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		pathFinder = new EntityPathFinder(MC.player);
		speed.resetTimer();
		MC.options.useKey.setPressed(true);
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		IKeyBinding.get(MC.options.useKey).resetPressedState();
		
		pathFinder = null;
		processor = null;
		ticksProcessing = 0;
		PathProcessor.releaseControls();
	}
	
	private long skyblockCooldownStart = 0;
	private long gardenCooldownStart = 0;
	private final int cooldown = 5000;
	private boolean skyblockOnCooldown = false;
	private boolean gardenOnCooldown = false;
	
	@Override
	public void onUpdate()
	{
		if(MC.player == null)
			return;
		MC.options.useKey.setPressed(true);
		
		long now = System.currentTimeMillis();
		int x = MC.player.getBlockX();
		int y = MC.player.getBlockY();
		int z = MC.player.getBlockZ();
		
		int randomExtra = (int)(Math.random() * 901) + 100;
		if(skyblockOnCooldown
			&& now - skyblockCooldownStart >= cooldown + randomExtra)
			skyblockOnCooldown = false;
		
		int randomExtra2 = (int)(Math.random() * 901) + 100;
		if(gardenOnCooldown
			&& now - gardenCooldownStart >= cooldown + randomExtra2)
			gardenOnCooldown = false;
		if(y == 75 && !skyblockOnCooldown && x != 48 && z != -47)
		{
			MC.player.networkHandler.sendChatCommand("skyblock");
			skyblockOnCooldown = true;
			skyblockCooldownStart = now;
		}else if(y == 70 && !gardenOnCooldown && x != 48 && z != -47)
		{
			MC.player.networkHandler.sendChatCommand("warp garden");
			gardenOnCooldown = true;
			gardenCooldownStart = now;
		}
		
		speed.updateTimer();
		
		if(pauseOnContainers.shouldPause())
			return;
		
		Stream<Entity> stream = EntityUtils.getAttackableEntities();
		stream = entityFilters.applyTo(stream);
		
		Entity entity = stream
			.min(
				Comparator.comparingDouble(e -> MC.player.squaredDistanceTo(e)))
			.orElse(null);
		if(entity == null)
			return;
		
		if(useAi.isChecked())
		{
			if((processor == null || processor.isDone() || ticksProcessing >= 10
				|| !pathFinder.isPathStillValid(processor.getIndex()))
				&& (pathFinder.isDone() || pathFinder.isFailed()))
			{
				pathFinder = new EntityPathFinder(entity);
				processor = null;
				ticksProcessing = 0;
			}
			
			if(!pathFinder.isDone() && !pathFinder.isFailed())
			{
				PathProcessor.lockControls();
				WURST.getRotationFaker()
					.faceVectorClient(entity.getBoundingBox().getCenter());
				pathFinder.think();
				pathFinder.formatPath();
				processor = pathFinder.getProcessor();
			}
			
			if(!processor.isDone())
			{
				processor.process();
				ticksProcessing++;
			}
		}else
		{
			if(MC.player.horizontalCollision && MC.player.isOnGround())
				MC.player.jump();
			
			if(MC.player.isTouchingWater() && MC.player.getY() < entity.getY())
				MC.player.addVelocity(0, 0.04, 0);
			
			if(!MC.player.isOnGround() && (MC.player.getAbilities().flying)
				&& MC.player.squaredDistanceTo(entity.getX(), MC.player.getY(),
					entity.getZ()) <= MC.player.squaredDistanceTo(
						MC.player.getX(), entity.getY(), MC.player.getZ()))
			{
				if(MC.player.getY() > entity.getY() + 1D)
					MC.options.sneakKey.setPressed(true);
				else if(MC.player.getY() < entity.getY() - 1D)
					MC.options.jumpKey.setPressed(true);
			}else
			{
				MC.options.sneakKey.setPressed(false);
				MC.options.jumpKey.setPressed(false);
			}
			
			MC.options.forwardKey.setPressed(
				MC.player.distanceTo(entity) > distance.getValueF());
			WURST.getRotationFaker()
				.faceVectorClient(entity.getBoundingBox().getCenter());
		}
		
		if(MC.player.squaredDistanceTo(entity) > Math.pow(range.getValue(), 2))
			return;
		
		if(MC.player.squaredDistanceTo(entity) > Math.pow(range.getValue(), 2))
			return;
	}
	
	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		PathCmd pathCmd = WURST.getCmds().pathCmd;
		pathFinder.renderPath(matrixStack, pathCmd.isDebugMode(),
			pathCmd.isDepthTest());
	}
	
	private class EntityPathFinder extends PathFinder
	{
		private final Entity entity;
		
		public EntityPathFinder(Entity entity)
		{
			super(BlockPos.ofFloored(entity.getPos()));
			this.entity = entity;
			setThinkTime(1);
		}
		
		@Override
		protected boolean checkDone()
		{
			return done =
				entity.squaredDistanceTo(Vec3d.ofCenter(current)) <= Math
					.pow(distance.getValue(), 2);
		}
		
		@Override
		public ArrayList<PathPos> formatPath()
		{
			if(!done)
				failed = true;
			
			return super.formatPath();
		}
	}
}
