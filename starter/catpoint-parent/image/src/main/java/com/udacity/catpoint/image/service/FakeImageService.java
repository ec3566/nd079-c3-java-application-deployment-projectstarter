package com.udacity.catpoint.image.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Service that tries to guess if an image displays a cat.
 */
public class FakeImageService implements ImageService {
	private final Random r = new Random ( );

	@Override
	public boolean imageContainCat ( BufferedImage image, float confidenceThreshhold ) {
		return r.nextBoolean ( );
	}
}
