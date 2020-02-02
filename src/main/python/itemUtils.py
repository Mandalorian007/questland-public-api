from dataclasses import dataclass
from typing import List
from enum import Enum


class ItemSlot(Enum):
    HELM = 1
    CHEST = 2
    GLOVES = 3
    BOOTS = 4
    NECKLACE = 5
    RING = 6
    TALISMAN = 7
    MAIN_HAND = 8
    OFF_HAND = 9


class LinkBonus(Enum):
    NONE = 1
    ATTACK = 2
    MAGIC = 3
    DEFENSE = 4


@dataclass
class Item:
    id: int
    name: str
    slot: ItemSlot
    potential: int
    attack: int
    magic: int
    defense: int
    health: int
    link_bonus: LinkBonus
    links: List[int]
    orb_bonus: LinkBonus
    orb_links: List[int]
    set: int


def text_to_link_bonus(text: str):
    if text == 'damage':
        return LinkBonus.ATTACK
    elif text == 'defence':
        return LinkBonus.DEFENSE
    elif text == 'magic':
        return LinkBonus.MAGIC
    else:
        return LinkBonus.NONE


def text_to_item_slot(text: str):
    if text == 'head':
        return ItemSlot.HELM
    elif text == 'chest':
        return ItemSlot.CHEST
    elif text == 'gloves':
        return ItemSlot.GLOVES
    elif text == 'feet':
        return ItemSlot.BOOTS
    elif text == 'amulet':
        return ItemSlot.NECKLACE
    elif text == 'ring':
        return ItemSlot.RING
    elif text == 'talisman':
        return ItemSlot.TALISMAN
    elif text == 'main_hand':
        return ItemSlot.MAIN_HAND
    elif text == 'off_hand':
        return ItemSlot.OFF_HAND
    else:
        raise ValueError(f'Unable to determine item slot from value: {text}')
