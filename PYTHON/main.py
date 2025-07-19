import pyautogui as pg
import time as t

def tabbin(okna):
    okna = int(okna)
    t.sleep(60 * 14.5)
    while True:
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

        t.sleep(60 * 14.3)

aplikacje = input("ilość instancji: ")
tabbin(aplikacje)