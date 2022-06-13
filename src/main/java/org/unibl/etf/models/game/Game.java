package org.unibl.etf.models.game;

import org.unibl.etf.models.pawn.Pawn;

import java.util.LinkedList;

public class Game extends Thread {
    LinkedList<String> playerNickNames;
    LinkedList<Pawn> pawns;
}
