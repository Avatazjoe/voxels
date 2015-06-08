package geometry

/**
 * Created by markus on 08/06/15.
 */
object Matrix4 {
  def rotationMatrix( angle: Double, vX: Double, vY: Double, vZ: Double ): Matrix4 = {
    val n = math.sqrt( vX*vX + vY*vY + vZ*vZ )
    val nX = vX / n
    val nY = vY / n
    val nZ = vZ / n
    val cosA = math.cos( angle )
    val sinA = math.sin( angle )
    Matrix4( cosA + nX*nX*(1-cosA),    nX*nY*(1-cosA) - nZ*sinA, nX*nZ*(1-cosA) + nY*sinA, 0
           , nX*nY*(1-cosA) + nZ*sinA, cosA + nY*nY*(1-cosA),    nY*nZ*(1-cosA) - nX*sinA, 0
           , nX*nZ*(1-cosA) - nY*sinA, nY*nZ*(1-cosA) + nX*sinA, cosA + nZ*nZ*(1-cosA),    0
           , 0,                        0,                        0,                        1 )
  }

  val unit = Matrix4(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1)
}

case class Matrix4( a00: Double, a01: Double, a02: Double, a03: Double
                  , a10: Double, a11: Double, a12: Double, a13: Double
                  , a20: Double, a21: Double, a22: Double, a23: Double
                  , a30: Double, a31: Double, a32: Double, a33: Double ) {
  def toSeqs =
    Seq( Seq( a00, a01, a02, a03 )
       , Seq( a10, a11, a12, a13 )
       , Seq( a20, a21, a22, a23 )
       , Seq( a30, a31, a32, a33 ) )

  def row( i: Int ) = {
    assert( i < 4 && i >= 0 )
    i match {
      case 0 => Vec4( a00, a01, a02, a03 )
      case 1 => Vec4( a10, a11, a12, a13 )
      case 2 => Vec4( a20, a21, a22, a23 )
      case 3 => Vec4( a30, a31, a32, a33 )
    }
  }

  def col( i: Int ) = {
    assert( i < 4 && i >= 0 )
    i match {
      case 0 => Vec4( a00, a10, a20, a30 )
      case 1 => Vec4( a01, a11, a21, a31 )
      case 2 => Vec4( a02, a12, a22, a32 )
      case 3 => Vec4( a03, a13, a23, a33 )
    }
  }

  def times( m: Matrix4 ) = Matrix4(
    row( 0 ) dot m.col( 0 ), row( 0 ) dot m.col( 1 ), row( 0 ) dot m.col( 2 ), row( 0 ) dot m.col( 3 ),
    row( 1 ) dot m.col( 0 ), row( 1 ) dot m.col( 1 ), row( 1 ) dot m.col( 2 ), row( 1 ) dot m.col( 3 ),
    row( 2 ) dot m.col( 0 ), row( 2 ) dot m.col( 1 ), row( 2 ) dot m.col( 2 ), row( 2 ) dot m.col( 3 ),
    row( 3 ) dot m.col( 0 ), row( 3 ) dot m.col( 1 ), row( 3 ) dot m.col( 2 ), row( 3 ) dot m.col( 3 )
  )

  def * = times _

  def apply( v: Vec3 ): Vec3 = apply( Vec4( v ) ).toVec3

  def apply( v: Vec4 ): Vec4 = {
    Vec4( row( 0 ) dot v
        , row( 1 ) dot v
        , row( 2 ) dot v
        , row( 3 ) dot v )
  }

}