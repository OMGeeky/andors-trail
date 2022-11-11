import json
import sys
from pathlib import Path
from xml.etree import ElementTree as etree


def convert(xml_path: Path, world_folder: Path):
    with open(xml_path, 'r') as xml_f:
        xml = etree.fromstring(xml_f.read())

    maps_folder = world_folder.parent

    for segment in xml.findall('segment'):
        segment_id = segment.get('id')
        world = {}

        maps = []
        for xml_map in segment.findall('map'):
            x = int(xml_map.get('x'))
            y = int(xml_map.get('y'))
            id = xml_map.get('id')
            with open(Path(maps_folder, f'{id}.tmx'), 'r') as map_f:
                map_tmx = etree.fromstring(map_f.read())

            tilewidth = int(map_tmx.get('tilewidth'))
            tileheight = int(map_tmx.get('tileheight'))
            width = int(map_tmx.get('width'))
            height = int(map_tmx.get('height'))

            map = {
                'fileName': f'../{id}.tmx',
                "height": width * tilewidth,
                "width": height * tileheight,
                'x': x * tilewidth,
                'y': y * tileheight,
            }
            maps.append(map)

        world['maps'] = maps
        world["onlyShowAdjacentMaps"] = False
        world["type"] = "world"
        with open(Path(world_folder, f'{segment_id}.world'), 'w') as f:
            json.dump(world, f, indent=4)


if __name__ == '__main__':
    if sys.argv:
        folder = Path(sys.argv[1])
    else:
        folder = Path('./res\\xml\\')
    convert(Path(folder, 'worldmap.xml'),
            Path(folder, 'worlds'))
