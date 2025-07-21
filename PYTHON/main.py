import pyautogui as pg
import time as t
import random as r

def tabbin(okna):
    okna = int(okna)
    while True:
        t.sleep(60*15)
        for i in range(okna+3):
            pg.keyDown('alt')
            for _ in range(i+1):
                pg.press('tab')
                t.sleep(1)
            pg.keyUp('alt')
            t.sleep(1)
            pg.click()
            t.sleep(1)
            pg.press("num5")
            t.sleep(1)
            pg.press("num5")
            t.sleep(1)

aplikacje = input("ilość instancji: ")
tabbin(aplikacje)