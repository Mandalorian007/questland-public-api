from itemsApiExtract import *
from itemUtils import *
import csv


def simplify_item(leg_item):
    if leg_item['n'].lower() == 'Ward Of Undead'.lower():
        print(leg_item)

    stats = leg_item['stats']
    # Items like weapons won't have links
    if len(leg_item['links']) > 0:
        link = leg_item['links'][0]['i']
        link_bonus = text_to_link_bonus(leg_item['links'][0]['e'])
        # Jubilee Talisman is an edge case with 1 link
        if len(link) == 1:
            item_links = [link[0][0]]
        else:
            item_links = [link[0][0], link[1][0], link[2][0]]
    else:
        item_links = []
        link_bonus = LinkBonus.NONE
    # orb links
    if len(leg_item['links']) > 1:
        orb_link_raw = leg_item['links'][1]['i']
        orb_links = []
        for orb_raw in orb_link_raw:
            orb_links.append(orb_raw[0])
    else:
        orb_links = []
    return Item(
        leg_item['t'],  # note we are going to treat the link id as the id
        leg_item['n'],
        text_to_item_slot(leg_item['s']),
        stats['dmg'][1] + stats['magic'][1] + stats['def'][1] + stats['hp'][1],
        stats['dmg'][0],
        stats['magic'][0],
        stats['def'][0],
        stats['hp'][0],
        link_bonus,
        item_links,
        LinkBonus.NONE,
        orb_links,
        leg_item['set']
    )

# Load server data for current in game items
server_items = get_server_items()

# filter for
# only legendary items
# Valid item types:
#   'talisman', 'ring', 'chest', 'head', 'off_hand', 'main_hand', 'amulet', 'gloves', 'feet'
legendary_items = \
    [x for x in server_items if x['q'] == 'legendary'
     and x['s'] not in ['usable', 'rune', 'giftbox', 'lockbox', 'exactshard']]

# Convert all the data into a simplified model for calculations
item_map = {}
for item in legendary_items:
    item_map[item['t']] = simplify_item(item)
item_list = list(item_map.values())

# Build orb map
orb_list = [x for x in server_items if x['q'] == 'legendary' and x['s'] == 'rune']
orb_map = {}

for orb in orb_list:
    orb_map[orb['t']] = orb['n']

with open('ql_export.csv', mode='w') as ql_export:
    csv_writer = csv.writer(ql_export, delimiter=',', quotechar='"', quoting=csv.QUOTE_NONE)

    csv_writer.writerow(['Item Name', 'Potential', 'Slot', 'Health', 'Attack', 'Magic', 'Defense',
                         'Set',
                         'Link 1', 'Link 2', 'Link 3', 'Orb 1', 'Orb 2'])

    for item in item_list:
        item_link_1 = ''
        if len(item.links) >= 1:
            item_link_1 = item_map[item.links[0]].name
        item_link_2 = ''
        if len(item.links) >= 2:
            item_link_2 = item_map[item.links[1]].name
        item_link_3 = ''
        if len(item.links) >= 3:
            item_link_3 = item_map[item.links[2]].name

        orb_1 = ''
        if len(item.orb_links) >= 1:
            if item.orb_links[0] in orb_map:
                orb_1 = orb_map[item.orb_links[0]]
        orb_2 = ''
        if len(item.orb_links) >= 2:
            if item.orb_links[1] in orb_map:
                orb_2 = orb_map[item.orb_links[1]]

        csv_writer.writerow([item.name, item.potential, item.slot,
                            item.health, item.attack, item.magic, item.defense, item.set,
                            item_link_1, item_link_2, item_link_3,
                            orb_1, orb_2])

