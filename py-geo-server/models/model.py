from datetime import datetime
from enum import Enum
from typing import Any

from pydantic import BaseModel


class AccountAuthorization(BaseModel):
    platform:str
    clientId:str
    timestamp:datetime
    dataDir:str

class PublishTask(BaseModel):
    clientId: str
    id:int
    fileList:list[str]
    declareAi:int
    dataDir:str
    contentMarkdown:str
    title:str
    platformNames:list[str]
    headless:int
SUCCESS_CODE = 1
ERROR_CODE = 0


class Status(Enum):
    NONE = -1
    STOP = 0
    RUN = 1

class R(BaseModel):
    type:str
    message:str
    code:int
    data:Any


def error1(message:str)->R:
    rst = R(type="",message=message,code=ERROR_CODE,data={})
    return rst

def ok3(message:str)->R:
    rst = R(type="",message=message,code=SUCCESS_CODE,data={})
    return rst

def ok2(type:str,message:str,data:Any)->R:
    rst = R(type=type,message=message,code=SUCCESS_CODE,data=data)
    return rst

def ok1(type:str,message:str)->R:
    rst = R(type=type,message=message,code=SUCCESS_CODE,data={})
    return rst