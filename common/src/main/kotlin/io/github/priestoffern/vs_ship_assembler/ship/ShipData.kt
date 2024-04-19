package io.github.priestoffern.vs_ship_assembler.ship


import org.joml.*
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.ShipTeleportData
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import java.util.*


class ShipData {
    var orientation: Quaterniond
    var position: Vector3d
    var dimension: Optional<String>
    var velocity: Optional<Vector3d> = Optional.empty<Vector3d>()
    var omega: Optional<Vector3d> = Optional.empty<Vector3d>()
    var scale = Optional.empty<Double>()

    constructor(
        orientation: Quaterniond,
        position: Vector3d,
        dimension: String?,
        velocity: Vector3d?,
        omega: Vector3d?,
        scalarScale: Double?
    ) {
        this.orientation = orientation
        this.position = position
        this.dimension = Optional.ofNullable(dimension)
        this.velocity = Optional.ofNullable<Vector3d>(velocity)
        this.omega = Optional.ofNullable<Vector3d>(omega)
        this.scale = Optional.ofNullable<Double>(scalarScale)
    }

    constructor(orientation: Quaterniond, position: Vector3d, dimension: String?) {
        this.orientation = orientation
        this.position = position
        this.dimension = Optional.ofNullable(dimension)
    }

    constructor(orientation: Quaterniondc, position: Vector3dc, dimension: String?) : this(
        Quaterniond(orientation.x(), orientation.y(), orientation.z(), orientation.w()),
        Vector3d(position.x(), position.y(), position.z()), dimension
    )

    constructor(transform: ShipTransform) : this(transform.shipToWorldRotation, transform.positionInWorld, null)

    constructor(ship: Ship) : this(ship.transform) {
        velocity = Optional.of(Vector3d(ship.velocity))
        omega = Optional.of(Vector3d(ship.omega))
    }

    constructor(ship: ServerShip, useGeometricCenter: Boolean) : this(ship) {
        if (useGeometricCenter) {
            val shipBound = ship.shipAABB
            val shipCoordBoundCenter: Vector3d = Vector3d()
                shipBound?.center(shipCoordBoundCenter)
            val shipCoordMassCenter: Vector3d =
                Vector3d(ship.inertiaData.centerOfMassInShip).add(Vector3d(0.5))
            val centerOfMassOffset: Vector3d = transformPosition(ship.transform, shipCoordMassCenter)
                .sub(transformPosition(ship.transform, shipCoordBoundCenter))
            position.sub(centerOfMassOffset)
        }
    }

    constructor(position: ShipData) : this(
        position.orientation,
        position.position,
        if (position.dimension.isPresent) position.dimension.get() else null,
        if (position.velocity.isPresent) position.velocity.get() else null,
        if (position.omega.isPresent) position.omega.get() else null,
        if (position.scale.isPresent) position.scale.get() else null
    )

    // Use Geometric center idk
    fun toShipTeleportData(ship: ServerShip): ShipTeleportData {
        val shipBound = ship.shipAABB
        val shipCoordBoundCenter: Vector3d = Vector3d()
        shipBound?.center(shipCoordBoundCenter)
        val shipCoordMassCenter: Vector3d =
            Vector3d(ship.inertiaData.centerOfMassInShip).add(Vector3d(0.5))
        val centerOfMassOffset: Vector3d = transformPosition(ship.transform, shipCoordMassCenter)
            .sub(transformPosition(ship.transform, shipCoordBoundCenter))
        val temp = ShipData(this)
        temp.position.add(centerOfMassOffset)
        return temp.toShipTeleportData()
    }

    fun toShipTeleportData(): ShipTeleportData {
        return ShipTeleportDataImpl(
            position,
            orientation,
            (if (velocity.isPresent) velocity else Vector3d()) as Vector3dc,
            (if (omega.isPresent) omega else Vector3d()) as Vector3dc,
            if (dimension.isPresent) dimension.get() else null,
            if (scale.isPresent) scale.get() else null
        )
    }

    fun toWorldPosition(transform: ShipTransform) {
        val quat = transform.shipToWorldRotation
        orientation = Quaterniond(quat.x(), quat.y(), quat.z(), quat.w()).mul(
            orientation
        )
        position = transformPosition(transform, position)
    }

    private fun transformPosition(ship: ShipTransform, pos: Vector3d): Vector3d {
        return ship.shipToWorld.transformPosition(pos)
    }
}