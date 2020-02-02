from itemUtils import *
from itemsApiExtract import *
from itertools import combinations


def type_validation(item: Item, required_slot: ItemSlot):
    if item.slot != required_slot:
        raise ValueError(f'{item.name} is not a {required_slot}.')


def is_item_linked(item: Item, all_socketed_items: List):
    matches = list(set(item.links) & set(all_socketed_items))
    return len(matches) >= 2


def simplify_item(leg_item):
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
    return Item(
        leg_item['t'],  # note we are going to treat the link id as the id
        leg_item['n'],
        text_to_item_slot(leg_item['s']),
        0,
        stats['dmg'][0],
        stats['magic'][0],
        stats['def'][0],
        stats['hp'][0],
        link_bonus,
        item_links,
        LinkBonus.NONE,
        [],
        0
    )


def select_top_n_stat(n: int, available_item_list: List, stat: LinkBonus):
    # item id, stat
    tuple_list = []

    if stat == LinkBonus.ATTACK:
        for item in available_item_list:
            if item.link_bonus == LinkBonus.ATTACK:
                tuple_list.append((item.id, item.attack))
    elif stat == LinkBonus.DEFENSE:
        for item in available_item_list:
            if item.link_bonus == LinkBonus.DEFENSE:
                tuple_list.append((item.id, item.defense))
    elif stat == LinkBonus.MAGIC:
        for item in available_item_list:
            if item.link_bonus == LinkBonus.MAGIC:
                tuple_list.append((item.id, item.magic))
    # Health
    else:
        for item in available_item_list:
            # TODO Attack items always have secondary good health ignoring weapons atm.
            if item.link_bonus == LinkBonus.ATTACK:
                tuple_list.append((item.id, item.health))

    # Sort list by highest stats
    tuple_list.sort(key=lambda x: x[1], reverse=True)
    item_id_list = list(map(lambda x: x[0], tuple_list))

    # select the top n from the sorted list
    return item_id_list[:n]


def score_combination(combination: List, equipped: List, item_map):
    all_socketed = equipped + combination

    # If all equipped gear isn't linked set the score to 0 to ensure the combination is ignored.
    if not all(is_item_linked(item_map[item], all_socketed) for item in equipped):
        return 0

    item_score_list = []
    for i in range(len(combination)):
        item = item_map[combination[i]]
        multiplier_bonus = 1

        # If links are met apply a 30% bonus
        if is_item_linked(item, all_socketed):
            multiplier_bonus = 1.3

        # i < 19 guards against accidentally confusing an hp item as an attack item
        if item.link_bonus == LinkBonus.ATTACK and i < 19:
            item_score_list.append(item.attack * multiplier_bonus)
        elif item.link_bonus == LinkBonus.DEFENSE:
            item_score_list.append(item.defense * multiplier_bonus)
        elif item.link_bonus == LinkBonus.MAGIC:
            item_score_list.append(item.magic * multiplier_bonus)
        else:
            item_score_list.append(item.health)

    return sum(item_score_list)


def solve_collections_for_equipped(
        helm: int, chest: int, gloves: int, boots: int, necklace: int, ring: int, talisman: int):
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
    for item in item_list:
        print(item)
    print(f'{len(item_list)} legendary items available for gear & collections.\n')

    print('Solving collections for gear:')
    print(f'helm: {item_map[helm].name}')
    print(f'chest: {item_map[chest].name}')
    print(f'gloves: {item_map[gloves].name}')
    print(f'boots: {item_map[boots].name}')
    print(f'necklace: {item_map[necklace].name}')
    print(f'ring: {item_map[ring].name}')
    print(f'talisman: {item_map[talisman].name}')

    # Some validation to make sure we have a valid gear profile
    type_validation(item_map[helm], ItemSlot.HELM)
    type_validation(item_map[chest], ItemSlot.CHEST)
    type_validation(item_map[gloves], ItemSlot.GLOVES)
    type_validation(item_map[boots], ItemSlot.BOOTS)
    type_validation(item_map[necklace], ItemSlot.NECKLACE)
    type_validation(item_map[ring], ItemSlot.RING)
    type_validation(item_map[talisman], ItemSlot.TALISMAN)
    equipped_items = [helm, chest, gloves, boots, necklace, ring, talisman]

    # Available items can't be equipped
    # (since items are sourced from separate pools no collection overlap)
    available_item_list = [item for item in item_list if item.id not in equipped_items]

    print('\nCalculating combinations:')

    available_attack_items = select_top_n_stat(7, available_item_list, LinkBonus.ATTACK)
    attack_item_combinations = [list(combo) for combo in combinations(available_attack_items, 5)]
    print(f'attack item combinations: {len(attack_item_combinations)}')

    available_magic_items = select_top_n_stat(7, available_item_list, LinkBonus.MAGIC)
    magic_item_combinations = [list(combo) for combo in combinations(available_magic_items, 5)]
    print(f'magic item combinations: {len(magic_item_combinations)}')

    available_defense_items = select_top_n_stat(7, available_item_list, LinkBonus.DEFENSE)
    defense_item_combinations = [list(combo) for combo in combinations(available_defense_items, 5)]
    print(f'defense item combinations: {len(defense_item_combinations)}')

    available_health_items = select_top_n_stat(12, available_item_list, LinkBonus.NONE)
    health_item_combinations = [list(combo) for combo in combinations(available_health_items, 5)]
    print(f'health item combinations: {len(health_item_combinations)}')

    final_combinations = []
    for attack_combo in attack_item_combinations:
        for magic_combo in magic_item_combinations:
            for defense_combo in defense_item_combinations:
                nonhealth_combo = attack_combo + magic_combo + defense_combo
                for health_combo in health_item_combinations:
                    if not any(item in health_combo for item in nonhealth_combo):
                        final_combinations.append(nonhealth_combo + health_combo)
    print(f'final number of combinations: {len(final_combinations)}')

    print('\nCalculating the best combination...')
    score_combination_tuple_list = []
    for combination in final_combinations:
        score = score_combination(combination, equipped_items, item_map)
        score_combination_tuple_list.append([score, combination])
    score_combination_tuple_list.sort(key=lambda x: x[0], reverse=True)

    if score_combination_tuple_list[0][0] == 0:
        print('No valid collection was discovered.')
        return

    combinations_ordered_by_score = list(map(lambda x: x[1], score_combination_tuple_list))

    print('\nTop Scoring combination: ')
    for i in range(len(combinations_ordered_by_score[0])):
        item = combinations_ordered_by_score[0][i]
        if i < 5:
            print(f'attack: {item_map[item].name}')
        elif i < 10:
            print(f'magic: {item_map[item].name}')
        elif i < 15:
            print(f'defense: {item_map[item].name}')
        elif i < 20:
            print(f'health: {item_map[item].name}')


solve_collections_for_equipped(11205, 11623, 11451, 11217, 11229, 11463, 10905)
