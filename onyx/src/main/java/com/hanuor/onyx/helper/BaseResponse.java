package com.hanuor.onyx.helper;

import com.hanuor.onyx.toolbox.gson.JsonElement;

/** Common envelope used by all Clarifai API responses. */
class BaseResponse {
  String statusCode;
  String statusMsg;
  JsonElement results;
}
