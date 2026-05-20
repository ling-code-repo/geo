from abc import abstractclassmethod, abstractmethod

from playwright.async_api import Page


class Publish():


    @abstractmethod
    async def publish(self, page: Page):
        pass