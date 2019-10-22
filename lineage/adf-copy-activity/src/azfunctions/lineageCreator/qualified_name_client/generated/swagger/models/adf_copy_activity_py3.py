# coding=utf-8
# --------------------------------------------------------------------------
# Code generated by Microsoft (R) AutoRest Code Generator.
# Changes may cause incorrect behavior and will be lost if the code is
# regenerated.
# --------------------------------------------------------------------------

from msrest.serialization import Model


class AdfCopyActivity(Model):
    """AdfCopyActivity.

    :param datafactory_name:
    :type datafactory_name: str
    :param pipeline_name:
    :type pipeline_name: str
    :param activity_name:
    :type activity_name: str
    """

    _attribute_map = {
        'datafactory_name': {'key': 'datafactory_name', 'type': 'str'},
        'pipeline_name': {'key': 'pipeline_name', 'type': 'str'},
        'activity_name': {'key': 'activity_name', 'type': 'str'},
    }

    def __init__(self, *, datafactory_name: str=None, pipeline_name: str=None, activity_name: str=None, **kwargs) -> None:
        super(AdfCopyActivity, self).__init__(**kwargs)
        self.datafactory_name = datafactory_name
        self.pipeline_name = pipeline_name
        self.activity_name = activity_name