package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;

public interface GameLogicListener {
    void onGarbageSent(int amount);
    void onAnnouncement(String text, BobColor color);
}
