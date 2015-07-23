package demo.webapp

import scala.scalajs.js.annotation.JSExport

/**
 * Created by markus on 11/06/15.
 */
@JSExport
object Shaders {

  // ***************** Common *****************

  private val header_common: String =
    """#ifdef GL_ES
      |#extension GL_OES_standard_derivatives : enable
      |#endif
    """.stripMargin

  // ***************** projections common *****************

  private val header_vertex_projs: String =
    """uniform mat4 u_mMat;
      |
      |varying float v_hemi0;
      |varying float v_hemi1;
    """.stripMargin

  private val body_vertex_projs: String =
    """vec4 rotPos = u_mMat * vec4(position, 1.0);
      |float longi = atan( rotPos.x, rotPos.z );
      |if (longi > 0.0) {
      |  v_hemi0 = 1.0; // east quadrant
      |} else {
      |  v_hemi0 = 0.0; // west quadrant
      |}
      |if (abs(longi) > 1.57079632679) {
      |  v_hemi1 = 1.0; // back quadrant
      |} else {
      |  v_hemi1 = 0.0; // front quadrant
      |}
    """.stripMargin

  private val header_fragment_projs: String =
    """varying float v_hemi0;
      |varying float v_hemi1;
    """.stripMargin

  private val body_fragment_projs: String => String =
    """if (v_hemi1 == 1.0 && ( v_hemi0 != 0.0 && v_hemi0 != 1.0 ) ) {
      |  // back hemisphere AND crossing east and west hemispheres
      |  gl_FragColor = vec4(0.0,0.0,0.0,0.0);
      |} else {
      |  gl_FragColor = vec4 (@@@color@@@, 1.0);
      |}
    """.stripMargin.replace( "@@@color@@@", _ )

  // ***************** Hammer-Aitoff projection *****************

  private val body_vertex_hammer_aitoff: String =
    """float lati = acos( rotPos.y );
      |float z = sqrt( 1.0 + sin( lati ) * cos( longi / 2.0 ) );
      |gl_Position = vec4( ( sin( lati ) * sin( longi / 2.0 ) ) / z, cos( lati ) / z, 0.0, 1.0);
    """.stripMargin

  // ***************** Spherical projection *****************

  private val body_vertex_spherical: String =
    "gl_Position = vec4( longi / 3.14159265359, log( ( 1.0+rotPos.y ) / ( 1.0-rotPos.y ) ) / 12.5663706144, 0.0, 1.0);"

  // ***************** programs *****************

  private def assembleShaderProgram( headers: List[String], bodyParts: List[String] ): String = {
    s"${ ( header_common :: headers ).mkString( "\n" ) }\nvoid main(){\n${ bodyParts.mkString( "\n" ) }\n}"
  }

  val cells_fragment_projs = assembleShaderProgram(
    List(
      """uniform vec3 u_borderColor;
        |uniform float u_borderWidth;
        |
        |varying vec3 v_color;
        |varying float v_centerFlag;
        |
        |float edgeFactor(const float thickness, const float centerFlag)
        |{
        |  return smoothstep(0.0, fwidth(centerFlag)*thickness, centerFlag);
        |}
      """.stripMargin,
      header_fragment_projs ),
    List( body_fragment_projs( "mix(u_borderColor, v_color, edgeFactor(u_borderWidth, v_centerFlag))" ) )
  )

  private def cells_vertex_projs( str: String ) = assembleShaderProgram(
    List(
      """attribute float a_centerFlag;
        |attribute vec3 a_color;
        |
        |varying vec3 v_color;
        |varying float v_centerFlag;
      """.stripMargin,
      header_vertex_projs
    ),
    List(
      body_vertex_projs,
      str,
      """v_color = a_color;
        |v_centerFlag = a_centerFlag;
      """.stripMargin
    )
  )

  val cells_vertex_spherical = cells_vertex_projs( body_vertex_spherical )

  val cells_vertex_hammer_aitoff = cells_vertex_projs( body_vertex_hammer_aitoff )

  val maze_fragment_projs = assembleShaderProgram(
    List(
      "uniform vec3 u_color;",
      header_fragment_projs ),
    List( body_fragment_projs( "u_color" ) )
  )

  private def maze_vertex_projs( str: String ): String = assembleShaderProgram(
    List(
      """attribute float a_centerFlag;
        |attribute vec3 a_color;
        |
        |varying vec3 v_color;
        |varying float v_centerFlag;
      """.stripMargin,
      header_vertex_projs
    ),
    List(
      body_vertex_projs,
      str
    )
  )

  val maze_vertex_spherical: String = maze_vertex_projs( body_vertex_spherical )

  val maze_vertex_hammer_aitoff: String = maze_vertex_projs( body_vertex_hammer_aitoff )
}
