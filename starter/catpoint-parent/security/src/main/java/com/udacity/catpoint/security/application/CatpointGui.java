package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * This is the primary JFrame for the application that contains all the top-level JPanels.
 *
 * We're not using any dependency injection framework, so this class also handles constructing
 * all our dependencies and providing them to other classes as necessary.
 */
public class CatpointGui extends JFrame {
    private SecurityRepository securityRepository = new PretendDatabaseSecurityRepositoryImpl();
    private FakeImageService imageService = new FakeImageService();
    private SecurityService securityService = new SecurityService(securityRepository, imageService);
    private com.udacity.catpoint.security.application.DisplayPanel displayPanel = new com.udacity.catpoint.security.application.DisplayPanel (securityService);
    private com.udacity.catpoint.security.application.ControlPanel controlPanel = new com.udacity.catpoint.security.application.ControlPanel (securityService);
    private com.udacity.catpoint.security.application.SensorPanel sensorPanel = new com.udacity.catpoint.security.application.SensorPanel (securityService);
    private com.udacity.catpoint.security.application.ImagePanel imagePanel = new com.udacity.catpoint.security.application.ImagePanel (securityService);

    public CatpointGui() {
        setLocation(100, 100);
        setSize(600, 850);
        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());
        mainPanel.add(displayPanel, "wrap");
        mainPanel.add(imagePanel, "wrap");
        mainPanel.add(controlPanel, "wrap");
        mainPanel.add(sensorPanel);

        getContentPane().add(mainPanel);

    }
}
