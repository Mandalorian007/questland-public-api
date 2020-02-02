import requests
import json


# Nice utility for dumping json
def jprint(obj):
    # create a formatted string of the Python JSON object
    text = json.dumps(obj, sort_keys=True, indent=4)
    print(text)


def get_server_items():
    # get latest item templates reference id
    latest_server_data_url = 'http://gs-bhs-wrk-02.api-ql.com/client/checkstaticdata/?lang=en&graphics_quality=hd_android'
    response = requests.get(latest_server_data_url).json()

    item_templates = response['data']['static_data']['crc_details']['item_templates']
    print(item_templates)

    # get all items
    items_url = f'http://gs-bhs-wrk-01.api-ql.com/staticdata/key/en/android/{item_templates}/item_templates/'
    return requests.get(items_url).json()


'''
Things I know about internal model
q = Gear quality common, uncommon, rare, legendary
s = socket for gear.  It looks like orbs, keys, and all kind of things are denoted here.
i = item id
t = item's link id
n = item's name
set = set identifier (don't know how to resolve set name)
links= first entry is gear second is orbs.  e: is type of bonus p is percent value. I is list of item ids
stats = obvious breakdown.  the second number is potential increase on each level.  Summing them all gives items actual potential
'''

''' Sample
{
    "ceff": [],
    "d": "",
    "g": 1000000,
    "hid": 0,
    "i": 1549,
    "i2": 13363,
    "i_hd": 21465,
    "i_sd": 21467,
    "ip": 40,
    "links": [
        {
            "e": "defence",
            "i": [
                [
                    1107,
                    1
                ],
                [
                    1104,
                    1
                ],
                [
                    1263,
                    1
                ]
            ],
            "p": 30
        },
        {
            "e": "damage",
            "i": [
                [
                    362,
                    1
                ],
                [
                    1419,
                    1
                ]
            ],
            "p": 15
        }
    ],
    "n": "Soul Eater Jaws",
    "p": 0,
    "prvw": 27883,
    "pskl": null,
    "pskls": [],
    "q": "legendary",
    "s": "head",
    "set": 3,
    "si": 1,
    "stats": {
        "def": [
            4535,
            46
        ],
        "dmg": [
            4157,
            42
        ],
        "hp": [
            3023,
            31
        ],
        "magic": [
            3401,
            34
        ]
    },
    "t": 1097,
    "umul": 1,
    "val": 0
}
'''

'''
Legendary orb object
'''

'''
{
    "d": "",
    "g": 1002,
    "i": 2163,
    "i_hd": 20463,
    "i_sd": 20465,
    "ip": 50,
    "n": "Behemoth Flames",
    "p": 0,
    "prvw": 27719,
    "q": "legendary",
    "s": "rune",
    "sd": 0,
    "si": 1,
    "stats": {
        "dmg": [
            96,
            31
        ]
    },
    "t": 357,
    "umul": 1
}
'''