package com.github.vroom.graph.particles;

import com.github.vroom.items.GameItem;

import java.util.List;

public interface IParticleEmitter {

    void cleanup();

    Particle getBaseParticle();

    List<GameItem> getParticles();
}
