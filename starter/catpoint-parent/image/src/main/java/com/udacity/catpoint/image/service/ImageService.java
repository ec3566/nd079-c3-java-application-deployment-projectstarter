package com.udacity.catpoint.image.service;

import java.awt.image.BufferedImage;

public interface ImageService {
	boolean imageContainCat ( BufferedImage image, float confidence );
}
