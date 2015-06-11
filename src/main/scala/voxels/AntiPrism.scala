package voxels

import geometry.Vec3
import voxels.Voxel.RegularPolygon

/**
 * Created by markus on 11/06/2015.
 */
case class AntiPrism( sides: Int ) extends VoxelStandard {
  assert( sides >= 3 )

  private val a: Double = math.sqrt( 3 )

  override val scale = {
    val x1 = math.cos( 2 * math.Pi / sides )
    val y1 = math.sin( 2 * math.Pi / sides )
    math.sqrt( ( 1 - x1 ) * ( 1 - x1 ) + y1 * y1 )
  }

  val vertices =
    ( 0 until sides ).flatMap { i =>
      val x0 = math.cos( i * 2 * math.Pi / sides )
      val y0 = math.sin( i * 2 * math.Pi / sides )
      val x1 = math.cos( ( 2*i+1 ) * math.Pi / sides )
      val y1 = math.sin( ( 2*i+1 ) * math.Pi / sides )
      Vec3( x0, y0, a * scale / 4 ) :: Vec3( x1, y1, -a * scale / 4 ) :: Nil
    }.toList

  val facesStructure = {
    val l = vertices.size
    ( ( 0 until l by 2 ).toList, RegularPolygon( l / 2 ), List( 0 ) ) ::
    ( ( 1 until l by 2 ).reverse.toList, RegularPolygon( l / 2 ), List( 0 ) ) ::
    Nil ++
    ( 0 until l ).map { i =>
      ( List( i, (i+1)%l, (i+2)%l ), RegularPolygon( 3 ), List( 0, 1, 2 ) )
    } ++
    ( 0 until l ).map { i =>
      ( List( i, (i+l-1)%l, (i+1)%l ), RegularPolygon( 3 ), List( 0, 1, 2 ) )
    }
  }

  override def name = s"$sides-sided antiprism"
}

