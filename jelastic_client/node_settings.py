from dataclasses import dataclass
from typing import List, Optional


@dataclass
class DockerSettings:
    image: str
    nodeGroup: Optional[str] = None


@dataclass
class NodeSettings:
    docker: Optional[DockerSettings] = None
    count: int = 1
    displayName: Optional[str] = None
    extip: bool = False
    fixedCloudlets: Optional[int] = None
    flexibleCloudlets: Optional[int] = None
    nodeType: Optional[str] = None


MultipleNodeSettings = List[NodeSettings]
