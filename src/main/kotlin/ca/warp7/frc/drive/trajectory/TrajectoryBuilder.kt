package ca.warp7.frc.drive.trajectory

import ca.warp7.frc.geometry.*

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryBuilder(builder: TrajectoryBuilder.() -> Unit) {

    init {
        builder(this)
    }

    internal var wheelbaseRadius = 0.0
    internal var trajectoryVelocity = 0.0
    internal var trajectoryAcceleration = 0.0
    internal var maxCentripetalAcceleration = 0.0
    internal var maxJerk = Double.POSITIVE_INFINITY
    internal var bendFactor = 1.2

    object NoFollower : TrajectoryFollower {
        override fun updateTrajectory(
                controller: TrajectoryController,
                setpoint: TrajectoryState,
                error: Pose2D
        ) {}
    }

    private var follower: TrajectoryFollower = NoFollower

    private val waypoints: MutableList<Pose2D> = mutableListOf()


    fun setFollower(f: TrajectoryFollower) {
        follower = f
    }

    fun wheelbaseRadius(metres: Double) {
        wheelbaseRadius = metres
    }

    fun trajectoryVelocity(metresPerSecond: Double) {
        trajectoryVelocity = metresPerSecond
    }

    fun trajectoryAcceleration(metresPerSecondSquared: Double) {
        trajectoryAcceleration = metresPerSecondSquared
    }

    fun jerkLimit(metresPerSecondCubed: Double) {
        maxJerk = metresPerSecondCubed
    }

    fun noJerkLimit() {
        maxJerk = Double.POSITIVE_INFINITY
    }

    fun centripetalAcceleration(hertz: Double) {
        maxCentripetalAcceleration = hertz
    }

    fun startAt(pose: Pose2D) {
        check(waypoints.isEmpty())
        waypoints.add(pose)
    }

    fun forward(metres: Double) {
        check(waypoints.isNotEmpty() && metres > 0)
        val pose = waypoints.last().run { Pose2D(translation + rotation.translation * metres, rotation) }
        waypoints.add(pose)
    }

    fun reverse(metres: Double) {
        check(waypoints.isNotEmpty() && metres > 0)
        val pose = waypoints.last().run { Pose2D(translation + rotation.translation * (-metres), rotation) }
        waypoints.add(pose)
    }

    fun turnRight(degrees: Double) {
        check(waypoints.isNotEmpty() && degrees > 0)
        val pose = waypoints.last().run { Pose2D(translation, rotation + Rotation2D.fromDegrees(-degrees)) }
        waypoints.add(pose)
    }

    fun turnLeft(degrees: Double) {
        check(waypoints.isNotEmpty() && degrees > 0)
        val pose = waypoints.last().run { Pose2D(translation, rotation + Rotation2D.fromDegrees(degrees)) }
        waypoints.add(pose)
    }

    fun moveTo(pose: Pose2D) {
        check(waypoints.isNotEmpty() && !pose.epsilonEquals(waypoints.last()))
        waypoints.add(pose)
    }

    fun move(t: Translation2D, r: Rotation2D = Rotation2D.identity) {
        // TODO make absolute/relative rotation consistent
        check(waypoints.isNotEmpty()
                && (!t.epsilonEquals(Translation2D.identity)
                || !r.epsilonEquals(Rotation2D.identity)))
        val pose = waypoints.last().run { Pose2D(translation + t, rotation + r) }
        waypoints.add(pose)
    }

    fun bendFactor(factor: Double) {
        bendFactor = factor
    }
}