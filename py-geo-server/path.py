import os
from pathlib import Path


class PathOperator:
    def __init__(self):
        temp = str(os.path.abspath(__file__))
        # logger.info(f"dir {temp}")
        temp = temp[0:temp.rindex(os.sep)]
        if temp.endswith("_internal"):
            temp = temp[0:temp.rindex(os.sep)]
        self.root_dir_path = temp

        # self.image_path = self.root_dir_path + os.sep + 'img'
        # if not os.path.exists(self.image_path):
        #     os.makedirs(self.image_path)

        # for f in os.listdir(self.image_path):
        #     if f.endswith('jpg'):
        #         os.remove(self.image_path + os.sep + f)
PATH = PathOperator()