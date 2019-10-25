# coding=utf-8
# --------------------------------------------------------------------------
# Code generated by Microsoft (R) AutoRest Code Generator.
# Changes may cause incorrect behavior and will be lost if the code is
# regenerated.
# --------------------------------------------------------------------------

from msrest.service_client import SDKClient
from msrest import Configuration, Serializer, Deserializer
from .version import VERSION
from msrest.pipeline import ClientRawResponse
from msrest.exceptions import HttpOperationError
from . import models


class QualifiedNameClientConfiguration(Configuration):
    """Configuration for QualifiedNameClient
    Note that all parameters used to create this instance are saved as instance
    attributes.

    :param str base_url: Service URL
    """

    def __init__(
            self, base_url=None):

        if not base_url:
            base_url = 'http://localhost'

        super(QualifiedNameClientConfiguration, self).__init__(base_url)

        self.add_user_agent('qualifiednameclient/{}'.format(VERSION))


class QualifiedNameClient(SDKClient):
    """defaultDescription

    :ivar config: Configuration for client.
    :vartype config: QualifiedNameClientConfiguration

    :param str base_url: Service URL
    """

    def __init__(
            self, base_url=None):

        self.config = QualifiedNameClientConfiguration(base_url)
        super(QualifiedNameClient, self).__init__(None, self.config)

        client_models = {k: v for k, v in models.__dict__.items() if isinstance(v, type)}
        self.api_version = '0.1'
        self._serialize = Serializer(client_models)
        self._deserialize = Deserializer(client_models)


    def get_request_format(
            self, code=None, type_name=None, custom_headers=None, raw=False, **operation_config):
        """

        :param code:
        :type code: str
        :param type_name:
        :type type_name: str
        :param dict custom_headers: headers that will be added to the request
        :param bool raw: returns the direct response alongside the
         deserialized response
        :param operation_config: :ref:`Operation configuration
         overrides<msrest:optionsforoperations>`.
        :return: RequestDescription or ClientRawResponse if raw=true
        :rtype: ~swagger.models.RequestDescription or
         ~msrest.pipeline.ClientRawResponse
        :raises:
         :class:`HttpOperationError<msrest.exceptions.HttpOperationError>`
        """
        # Construct URL
        url = self.get_request_format.metadata['url']

        # Construct parameters
        query_parameters = {}
        if code is not None:
            query_parameters['code'] = self._serialize.query("code", code, 'str')
        if type_name is not None:
            query_parameters['typeName'] = self._serialize.query("type_name", type_name, 'str')

        # Construct headers
        header_parameters = {}
        header_parameters['Accept'] = 'application/json'
        if custom_headers:
            header_parameters.update(custom_headers)

        # Construct and send request
        request = self._client.get(url, query_parameters, header_parameters)
        response = self._client.send(request, stream=False, **operation_config)

        if response.status_code not in [200]:
            raise HttpOperationError(self._deserialize, response)

        deserialized = None

        if response.status_code == 200:
            deserialized = self._deserialize('RequestDescription', response)

        if raw:
            client_raw_response = ClientRawResponse(deserialized, response)
            return client_raw_response

        return deserialized
    get_request_format.metadata = {'url': '/api/metadata-qualifiedname-service'}

    def get_qualified_name(
            self, code=None, type_name=None, body=None, custom_headers=None, raw=False, **operation_config):
        """

        :param code:
        :type code: str
        :param type_name:
        :type type_name: str
        :param body:
        :type body: ~swagger.models.Body
        :param dict custom_headers: headers that will be added to the request
        :param bool raw: returns the direct response alongside the
         deserialized response
        :param operation_config: :ref:`Operation configuration
         overrides<msrest:optionsforoperations>`.
        :return: QualifiedName or ClientRawResponse if raw=true
        :rtype: ~swagger.models.QualifiedName or
         ~msrest.pipeline.ClientRawResponse
        :raises:
         :class:`HttpOperationError<msrest.exceptions.HttpOperationError>`
        """
        # Construct URL
        url = self.get_qualified_name.metadata['url']

        # Construct parameters
        query_parameters = {}
        if code is not None:
            query_parameters['code'] = self._serialize.query("code", code, 'str')
        if type_name is not None:
            query_parameters['typeName'] = self._serialize.query("type_name", type_name, 'str')

        # Construct headers
        header_parameters = {}
        header_parameters['Accept'] = 'application/json'
        header_parameters['Content-Type'] = 'application/json; charset=utf-8'
        if custom_headers:
            header_parameters.update(custom_headers)

        # Construct body
        if body is not None:
            body_content = self._serialize.body(body, 'Body')
        else:
            body_content = None

        # Construct and send request
        request = self._client.post(url, query_parameters, header_parameters, body_content)
        response = self._client.send(request, stream=False, **operation_config)

        if response.status_code not in [200]:
            raise HttpOperationError(self._deserialize, response)

        deserialized = None

        if response.status_code == 200:
            deserialized = self._deserialize('QualifiedName', response)

        if raw:
            client_raw_response = ClientRawResponse(deserialized, response)
            return client_raw_response

        return deserialized
    get_qualified_name.metadata = {'url': '/api/metadata-qualifiedname-service'}