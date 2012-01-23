package com.turbonips.troglodytes.spatials;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.turbonips.troglodytes.components.AnimationCreature;
import com.turbonips.troglodytes.components.Sliding;

public class Player extends Spatial {
	private Sliding sliding;
	private Image sprite;
	private AnimationCreature animationCreature;
	private final GameContainer container;

	public Player(World world, Entity owner, Object form, GameContainer container) {
		super(world, owner);
		this.sprite = (Image)form;
		this.container = container;
	}

	@Override
	public void initalize() {
		ComponentMapper<Sliding> slidingMapper = new ComponentMapper<Sliding>(Sliding.class, world);
		sliding = slidingMapper.get(owner);
		ComponentMapper<AnimationCreature> animationCreatureMapper = new ComponentMapper<AnimationCreature>(AnimationCreature.class, world);
		animationCreature = animationCreatureMapper.get(owner);
	}

	@Override
	public void render(Graphics g) {
		//TODO: Make the commented out code work because it is more correct
		if (sliding != null) {
			if (animationCreature != null)
			{
				g.drawAnimation(animationCreature.getCurrent(), container.getWidth()/2 - sliding.getX(), container.getHeight()/2 - sliding.getY());
			}
			else
			{
				g.drawImage(sprite, container.getWidth()/2 - sliding.getX(), container.getHeight()/2 - sliding.getY());
			}
		} else {
			if (animationCreature != null)
			{
				g.drawAnimation(animationCreature.getCurrent(), container.getWidth()/2, container.getHeight()/2);
			}
			else
			{
				g.drawImage(sprite, container.getWidth()/2, container.getHeight()/2);
			}
		}
	}

}
