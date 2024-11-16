package com.javarush.task.jdk13.task53.task5302;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void openTile(int x, int y) {
        if (!isGameStopped) {
            GameObject gameObject = gameField[y][x];   // работа с каждой ячейкой по клику на неё
            if (!(gameObject.isFlag || gameObject.isOpen)) {
                gameObject.isOpen = true;
                countClosedTiles--;
                setCellColor(x, y, Color.GREEN);
                if (gameObject.isMine) {
                    setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
                    gameOver();
                } else if (gameObject.countMineNeighbors != 0) {
                    setCellNumber(x, y, gameObject.countMineNeighbors);
                    score = score + 5;
                    setScore(score);
                } else { // все кто рядом 0 - открыты
                    setCellValue(gameObject.x, gameObject.y, "");
                    List<GameObject> neighbors = getNeighbors(gameObject); //все 8 соседей
                    for (GameObject neighbor : neighbors) { // у каждого свои координаты
                        if (!neighbor.isOpen) {
                            //if (neighbor.countMineNeighbors == 0) {
                            openTile(neighbor.x, neighbor.y);
                            //}
                        }
                    }
                    setCellColor(x, y, Color.GREEN);
                    score = score + 5;
                    setScore(score);
                }
            }
            if (countClosedTiles == countMinesOnField && !gameObject.isMine) {
                win();
            }

        }


    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            GameObject gameObject = gameField[y][x];

            if (!gameObject.isFlag && !gameObject.isOpen && countFlags > 0) {
                setCellValue(gameObject.x, gameObject.y, FLAG);
                setCellColor(gameObject.x, gameObject.y, Color.DEEPPINK);
                gameObject.isFlag = true;
                countFlags--;
                showMessageDialog(Color.ALICEBLUE, "CountFlags: " + countFlags, Color.BLACK, 50);
            } else if (gameObject.isFlag && gameObject.isOpen) {  //Разобраться с countFlags
                gameObject.isFlag = false;
                countFlags++;
            } else if (gameObject.isFlag) {
                setCellValue(gameObject.x, gameObject.y, "");
                setCellColor(gameObject.x, gameObject.y, Color.GOLDENROD);
                gameObject.isFlag = false;
                countFlags++;
            }
        } else {
            return;
        }

    }

    private void win() {
        showMessageDialog(Color.ALICEBLUE, "You won!", Color.CORAL, 50);
        isGameStopped = true;
    }


    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        countFlags = 0;
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);

                setCellValue(x, y, "");
                setCellColor(x, y, Color.GOLDENROD);

            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;

    }

    private void gameOver() {
        showMessageDialog(Color.CHOCOLATE, "You lost", Color.AZURE, 50);
        isGameStopped = true;
    }

    private void countMineNeighbors(){
        List<GameObject> neighbors;
        int countMines = 0;
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                if (!gameField[i][j].isMine) {
                    neighbors = getNeighbors(gameField[i][j]);
                    //System.out.println("gameField[" +i + "] ["+ j + "] " + neighbors.size());
                    for (int k = 0; k < neighbors.size(); k++) {
                        countMines += neighbors.get(k).isMine? 1 : 0;
                    }
                    gameField[i][j].countMineNeighbors = countMines;
                    countMines = 0;

                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}