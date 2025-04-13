package org.opentmf.camunda.test.unit;

import static org.opentmf.camunda.test.util.VariableUtil.stringVariable;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Gokhan Demir
 */
class BpmUnitTest extends BaseBpmUnitTest {

  /**
   * <pre>
   * username: user-has-role
   * token: Bearer eyJhbGciOiJSUz ..... b3u2Dci4Rt0ilsqTg8h-ml1F1A
   * </pre>
   */
  private static final String VERY_LONG_STRING_VALUE_1 = "eyJ1c2VybmFtZSI6ICJ1c2VyLWhhcy1yb2xlIiwid"
      + "G9rZW4iOiAiQmVhcmVyIGV5SmhiR2NpT2lKU1V6STFOaUlzSW5SNWNDSWdPaUFpU2xkVUlpd2lhMmxrSWlBNklDSml"
      + "PV1pvUlU0eFdtSldXVXRXZFdWaGQyZFhZa0ZoUVRRMU9VRkJWMFpqV0hWMFJrRjJkelp1TFV4RkluMC5leUpsZUhBa"
      + "U9qRTNNVEV3TlRBM05qSXNJbWxoZENJNk1UY3hNVEEwTnpFMk1pd2lhblJwSWpvaU0yWTRPV1F5T1dVdFkyRXlOUzA"
      + "wWXprNUxXSTBOMlV0WXpSaE16bGpZbVF5TUdFM0lpd2lhWE56SWpvaWFIUjBjSE02THk5T1QxUXRVMFZVTGs1UFZDM"
      + "VRSVlF2Y21WaGJHMXpMMjl5WW1sMFlXNTBMWEpsWVd4dElpd2lZWFZrSWpwYkluSmxZV3h0TFcxaGJtRm5aVzFsYm5"
      + "RaUxDSmljbTlyWlhJaUxDSmhZMk52ZFc1MElsMHNJbk4xWWlJNklqQmpZV1JpTnpFMExXWXlZekF0TkdNd09DMDVaR"
      + "1kyTFdFMk9HWTVaRFF5TW1Rd1lTSXNJblI1Y0NJNklrSmxZWEpsY2lJc0ltRjZjQ0k2SW05eVltbDBZVzUwTFdKaFk"
      + "ydGxibVF0WTJ4cFpXNTBJaXdpYzJWemMybHZibDl6ZEdGMFpTSTZJalF3TjJSalltSTVMV1JpWm1JdE5Ea3pNaTFpT"
      + "kRCakxURXdZamRtTkRRMk0yUXdaaUlzSW5KbFlXeHRYMkZqWTJWemN5STZleUp5YjJ4bGN5STZXeUprWldaaGRXeDB"
      + "MWEp2YkdWekxXOXlZbWwwWVc1MExYSmxZV3h0SWl3aWIyWm1iR2x1WlY5aFkyTmxjM01pTENKMWJXRmZZWFYwYUc5e"
      + "WFYcGhkR2x2YmlKZGZTd2ljbVZ6YjNWeVkyVmZZV05qWlhOeklqcDdJbkpsWVd4dExXMWhibUZuWlcxbGJuUWlPbnN"
      + "pY205c1pYTWlPbHNpZG1sbGR5MXlaV0ZzYlNJc0luWnBaWGN0YVdSbGJuUnBkSGt0Y0hKdmRtbGtaWEp6SWl3aWJXR"
      + "nVZV2RsTFdsa1pXNTBhWFI1TFhCeWIzWnBaR1Z5Y3lJc0ltbHRjR1Z5YzI5dVlYUnBiMjRpTENKeVpXRnNiUzFoWkc"
      + "xcGJpSXNJbU55WldGMFpTMWpiR2xsYm5RaUxDSnRZVzVoWjJVdGRYTmxjbk1pTENKeGRXVnllUzF5WldGc2JYTWlMQ"
      + "0oxYldGZmNISnZkR1ZqZEdsdmJpSXNJblpwWlhjdFlYVjBhRzl5YVhwaGRHbHZiaUlzSW5GMVpYSjVMV05zYVdWdWR"
      + "ITWlMQ0p4ZFdWeWVTMTFjMlZ5Y3lJc0ltMWhibUZuWlMxbGRtVnVkSE1pTENKdFlXNWhaMlV0Y21WaGJHMGlMQ0oyY"
      + "VdWM0xXVjJaVzUwY3lJc0luWnBaWGN0ZFhObGNuTWlMQ0oyYVdWM0xXTnNhV1Z1ZEhNaUxDSnRZVzVoWjJVdFlYVjB"
      + "hRzl5YVhwaGRHbHZiaUlzSW0xaGJtRm5aUzFqYkdsbGJuUnpJaXdpY1hWbGNua3RaM0p2ZFhCeklsMTlMQ0p2Y21Kc"
      + "GRHRnVkQzFpWVdOclpXNWtMV05zYVdWdWRDSTZleUp5YjJ4bGN5STZXeUp5WlhOdmRYSmpaUzFqWVhSaGJHOW5JaXd"
      + "pY1hWdmRHVXRiV0Z1WVdkbGJXVnVkQ0lzSW5CaGNuUnVaWEp6YUdsd0xXMWhibUZuWlcxbGJuUWlMQ0pxYjJJdGMyT"
      + "m9aV1IxYkdWeUlpd2ljR0Z5ZEhsU2IyeGxMVzFoYm1GblpXMWxiblFpTENKelpYSjJhV05sTFc5eVpHVnlMV1oxYkd"
      + "acGJHeHRaVzUwSWl3aWMyRnNaWE10YldGdVlXZGxiV1Z1ZENJc0luQnlhV05sTFdWdVoybHVaU0lzSW5ObGNuWnBZM"
      + "lV0YjNKa1pYSWlMQ0p3Y205a2RXTjBMVzl5WkdWeWFXNW5MV1oxYkdacGJHeHRaVzUwTFc5eVkyaGxjM1J5WVhScGI"
      + "yNGlMQ0pqZFhOMGIyMWxjaTF0WVc1aFoyVnRaVzUwSWl3aWMyaHZjSEJwYm1jdFkyRnlkQ0lzSW5GMWIzUmxJaXdpW"
      + "TNWemRHOXRaWEl0YW05MWNtNWxlU0lzSW1GamRHbDJhWFI1TFdocGMzUnZjbmtpTENKd1lYbHRaVzUwSWl3aWNtVnp"
      + "iM1Z5WTJVdGFXNTJaVzUwYjNKNUlpd2ljbVZ6YjNWeVkyVXRiM0prWlhKcGJtY2lMQ0p5ZFd4bExXVnVaMmx1WlNJc"
      + "0luQnliMjF2ZEdsdmJpMXRZVzVoWjJWdFpXNTBJaXdpY0hKdlpIVmpkQzF2Y21SbGNpSXNJbkJ5YjJSMVkzUXRZMkY"
      + "wWVd4dlp5SXNJbkpsYzI5MWNtTmxMVzl5WkdWeUxXWjFiR1pwYkd4dFpXNTBJaXdpY0dGNWJXVnVkQzF0WlhSb2IyU"
      + "WlMQ0p2Y21SbGNpMW5aVzVsY21GMGFXOXVJaXdpZFcxaFgzQnliM1JsWTNScGIyNGlMQ0p5YjJ4bGN5MWhibVF0Y0d"
      + "WeWJXbHpjMmx2Ym5NaUxDSnlaV1psY21WdVkyVXRiV0Z1WVdkbGJXVnVkQ0lzSW5OMGIzSmhaMlV0YzJWeWRtbGpaU"
      + "0lzSW5ObGNuWnBZMlV0YVc1MlpXNTBiM0o1SWl3aWNISnZaSFZqZEMxdlptWmxjbWx1WnkxeGRXRnNhV1pwWTJGMGF"
      + "XOXVJaXdpWjJWdlozSmhjR2hwWXkxaFpHUnlaWE56TFcxaGJtRm5aVzFsYm5RaUxDSndjbTlrZFdOMExXTmhkR0ZzY"
      + "jJjdGJXRnVZV2RsYldWdWRDSXNJbk5sY25acFkyVXRiM0prWlhKcGJtY3RablZzWm1sc2JHMWxiblF0YjNKamFHVnp"
      + "kSEpoZEdsdmJpSXNJbUpoWTJ0dlptWnBZMlYwWVhOckxXMWhibUZuWlcxbGJuUWlMQ0pvY21WbUxXMWhjQzF0WVc1a"
      + "FoyVnRaVzUwSWl3aVlXZHlaV1Z0Wlc1MExXMWhibUZuWlcxbGJuUWlMQ0p3Y205a2RXTjBMV2x1ZG1WdWRHOXllUzF"
      + "0WVc1aFoyVnRaVzUwSWl3aWNtVnpiM1Z5WTJVdGIzSmtaWEpwYm1jdFpuVnNabWxzYkcxbGJuUXRiM0pqYUdWemRIS"
      + "mhkR2x2YmlJc0luQnliMlIxWTNRdGIzSmtaWEl0Wm5Wc1ptbHNiRzFsYm5RaUxDSndZWEowZVMxdFlXNWhaMlZ0Wlc"
      + "1MElpd2lZV05qYjNWdWRDMXRZVzVoWjJWdFpXNTBJaXdpYzJWeWRtbGpaUzFqWVhSaGJHOW5JaXdpWkdGMFlTMW9hW"
      + "E4wYjNKNUxXMWhibUZuWlcxbGJuUWlMQ0prYjJOMWJXVnVkQzF0WVc1aFoyVnRaVzUwSWl3aWNHRnlkSGt0YVc1MFp"
      + "YSmhZM1JwYjI0dGJXRnVZV2RsYldWdWRDSXNJbU5oYzJVdGJXRnVZV2RsYldWdWRDSmRmU3dpWW5KdmEyVnlJanA3S"
      + "W5KdmJHVnpJanBiSW5KbFlXUXRkRzlyWlc0aVhYMHNJbUZqWTI5MWJuUWlPbnNpY205c1pYTWlPbHNpYldGdVlXZGx"
      + "MV0ZqWTI5MWJuUWlMQ0oyYVdWM0xXRndjR3hwWTJGMGFXOXVjeUlzSW5acFpYY3RZMjl1YzJWdWRDSXNJblpwWlhjd"
      + "FozSnZkWEJ6SWl3aWJXRnVZV2RsTFdGalkyOTFiblF0YkdsdWEzTWlMQ0prWld4bGRHVXRZV05qYjNWdWRDSXNJbTF"
      + "oYm1GblpTMWpiMjV6Wlc1MElpd2lkbWxsZHkxd2NtOW1hV3hsSWwxOWZTd2ljMk52Y0dVaU9pSndjbTltYVd4bElHV"
      + "nRZV2xzSWl3aWMybGtJam9pTkRBM1pHTmlZamt0WkdKbVlpMDBPVE15TFdJME1HTXRNVEJpTjJZME5EWXpaREJtSWl"
      + "3aVpXMWhhV3hmZG1WeWFXWnBaV1FpT21aaGJITmxMQ0p1WVcxbElqb2lkWE5sY2kxb1lYTXRjbTlzWlNCc1lYTjBJR"
      + "zVoYldVaUxDSm5jbTkxY0hNaU9sdGRMQ0p3Y21WbVpYSnlaV1JmZFhObGNtNWhiV1VpT2lKMWMyVnlMV2hoY3kxeWI"
      + "yeGxJaXdpWjJsMlpXNWZibUZ0WlNJNkluVnpaWEl0YUdGekxYSnZiR1VpTENKbVlXMXBiSGxmYm1GdFpTSTZJbXhoY"
      + "zNRZ2JtRnRaU0o5LkI3NmF2bXZyYk9Ea2xObE1OMkV2NHlBMDFKQW5yOEltRWJ4Vll1QUxkWFRWaFU0Z0pZLVdNeE9"
      + "aX1N2TUdGbVRKOUhMcE1jaVZZYmwyZmJMc2x3MGFqM3ZvX3gweGtVZ3cxZzltcHhnd1pEd1hvZHprMmRjTnlucFotZ"
      + "DJDM294VEJRYnQwenhtWmNZamM5blZtWl82YlNFaXYwNmc4QnR0RVo4Z2pnODk4MnVZcDg0eS1vUEFLQTlNbC1MY3c"
      + "zdDlzSjdsV0xxcW5hNWR2NkdiR0poSVlfQk9wYzY1bjZiZGRIVUIzNHcxYncyV1ZTMF9ETnZ4N1p1cFN3RklSV1F6R"
      + "HhsWHdzc18yVmV0cndqZzBROTRIalctMXNVQ2w2dGtwWC1TSU5VTFZIcEQ2b01SYWJjZUVEdWE4aFpiM3UyRGNpNFJ"
      + "0MGlsc3FUZzhoLW1sMUYxQSJ9";

  private static final Map<String, Object> VARIABLE_MAP = new HashMap<>() {{
    put("typedValueField", stringVariable(VERY_LONG_STRING_VALUE_1));
    put("String", stringVariable("short string value"));
    put("null", stringVariable(null));
  }};

  @Test
  void testBuildExternalTask_withValidVariables_buildsValidExternalTask() {
    Assertions.assertDoesNotThrow(() -> buildExternalTask(VARIABLE_MAP));
  }
}
