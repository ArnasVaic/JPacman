package com.pacman;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// Singleton design pattern
public class Assets {

    public static Assets instance = null;

    public static BufferedImage metadata;

    public static volatile AtomicInteger loadedResources;

    public static final int NUM_OF_FOOD_TYPES = 2;

    // food[0] - small food image
    // food[1] - large food image
    public static BufferedImage[] food;
    // Image of the map
    public static BufferedImage map;

    public static BufferedImage pacman;

    public static final int NUM_OF_GHOSTS = 4;

    // ghost targets
    // targets[0] - red target
    // targets[1] - pink target
    // targets[2] - yellow target
    // targets[3] - cyan target
    public static BufferedImage[] targets;

    // ghost images
    // ghosts[0] - red ghost
    // ghosts[1] - pink ghost
    // ghosts[2] - yellow ghost
    // ghosts[3] - cyan ghost
    public static BufferedImage[] ghosts;

    // frightened ghost texture sheet
    public static volatile boolean frigLoaded = false;
    public static volatile BufferedImage frightened;
    // ghost eyes texture sheet
    public static volatile boolean eyesLoaded = false;
    public static volatile BufferedImage eyes;

    public static volatile boolean ghostsLoaded = false;
    public static volatile AnimationController[] ghostAnim;

    public static AnimationController frigAnim;
    public static AnimationController eyesAnim;

    public static final Point GHOST_FRAME_COUNT = new Point(2, 4);
    public static final Point GHOST_SPRITE_SIZE = new Point(16, 16);
    public static final double GHOST_FRAME_TIME = 5.0;
    public static final float FONT_SIZE = 8f * Game.SCALE;

    public static volatile Font font;

    // 4 ghosts sheets + frightened + eyes
    public static final int TOTAL_GHOST_SHEETS = NUM_OF_GHOSTS + 2;
    public static volatile AtomicInteger ghostSheetsLoaded = new AtomicInteger(0);

    public static final Font DEFAULT_FONT = new Font("Calibri", Font.BOLD, 12);

    static Assets getInstance() {
        if(instance == null) instance = new Assets();
        return instance;
    }

    public void load() throws AssetNotFoundException {
        loadedResources = new AtomicInteger(0);
        ArrayList<Thread> threads = new ArrayList<>();
        ghosts = new BufferedImage[NUM_OF_GHOSTS];
        targets = new BufferedImage[NUM_OF_GHOSTS];
        food = new BufferedImage[NUM_OF_FOOD_TYPES];
        ghostAnim = new AnimationController[NUM_OF_GHOSTS];

        threads.add(new Thread(this::loadAnimations));

        threads.add(new Thread(() -> font = loadFontFromFile("/fonts/emulogic.ttf", FONT_SIZE)));

        threads.add(new Thread(() -> instance.loadGhostsAndTargets()));
        threads.add(new Thread(() -> {
            try {
                food[0] = loadImageFromFile("assets/f0.png");
            } catch(AssetNotFoundException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                food[1] = loadImageFromFile("assets/f1.png");
            } catch(AssetNotFoundException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                map = loadImageFromFile("assets/map.png");
            } catch(AssetNotFoundException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                pacman = loadImageFromFile("assets/p0.png");
            } catch(AssetNotFoundException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                metadata = loadImageFromFile("assets/metadata.png");
            } catch(AssetNotFoundException e) {
                e.printStackTrace();
            }
        }));
        for(Thread t: threads) {
            t.start();
        }
        for(Thread t: threads) {
            try {
                t.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Total resources loaded: %d\n", loadedResources.get());
    }

    public static BufferedImage loadImageFromFile(String path) throws AssetNotFoundException {
        BufferedImage img;
        try {
            File f = new File(path);
            img = ImageIO.read(f);
            synchronized (System.out) {
                System.out.printf("Image loaded: %s\n", path);
            }
            loadedResources.incrementAndGet();
        } catch(IOException e) {
            synchronized (System.out) {
                System.out.printf("Could not load image: %s\n", path);
            }
            throw new AssetNotFoundException(path);
        }
        return img;
    }

    public static Font loadFontFromFile(String path, float size) {
        Font font;
        try {
            InputStream is = Assets.class.getResourceAsStream(path);
            assert(is != null);
            font = Font.createFont(Font.PLAIN, is).deriveFont(size);
            synchronized (System.out) {
                System.out.printf("Font loaded: %s\n", path);
            }
            loadedResources.incrementAndGet();
        } catch (Exception e) {
            e.printStackTrace();
            synchronized (System.out) {
                System.out.printf("Could not load font: %s\n", path);
            }
            font = DEFAULT_FONT;
        }
        return font;
    }

    public synchronized void loadAnimations() {
        while(!ghostsLoaded) {
            try {
                wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i < ghosts.length; ++i) {
            ghostAnim[i] = new AnimationController(ghosts[i], Assets.GHOST_FRAME_COUNT, Assets.GHOST_SPRITE_SIZE, Assets.GHOST_FRAME_TIME);
        }

        while(!frigLoaded) {
            try {
                wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        frigAnim = new AnimationController(frightened, Assets.GHOST_FRAME_COUNT, Assets.GHOST_SPRITE_SIZE, Assets.GHOST_FRAME_TIME);

        while(!eyesLoaded) {
            try {
                wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        eyesAnim = new AnimationController(eyes, Assets.GHOST_FRAME_COUNT, Assets.GHOST_SPRITE_SIZE, Assets.GHOST_FRAME_TIME);
    }

    public synchronized void loadGhostsAndTargets()  {
        for (int i = 0; i < NUM_OF_GHOSTS; ++i) {
            String fileName = i + ".png";
            try {
                ghosts[i] = loadImageFromFile("assets/g" + fileName);
                notifyAll();
            } catch(AssetNotFoundException e) {
                e.printStackTrace();
            }
            try {
                targets[i] = loadImageFromFile("assets/t" + fileName);
            } catch (AssetNotFoundException e) {
                e.printStackTrace();
            }
        }
        ghostsLoaded = true;
        // Wake up the thread that is going to create animations
        notifyAll();

        try {
            frightened = loadImageFromFile("assets/gf.png");
            frigLoaded = true;
            // Wake up the thread that is going to create animations
            notifyAll();
        } catch(AssetNotFoundException e) {
            e.printStackTrace();
        }

        try {
            eyes = loadImageFromFile("assets/eyes.png");
            eyesLoaded = true;
            // Wake up the thread that is going to create animations
            notifyAll();
        } catch(AssetNotFoundException e) {
            e.printStackTrace();
        }

    }
}
