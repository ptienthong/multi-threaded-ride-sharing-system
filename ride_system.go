package main

import (
	"fmt"
	"sync"
	"time"
)

var rideIDCounter, riderIDCounter, driverIDCounter int
var rideIDMutex, riderIDMutex, driverIDMutex sync.Mutex

// ===== Ride Interface and Structs =====
type Ride interface {
	CalculateFare() float64
	PrintDetails()
}

type BaseRide struct {
	id      int
	pickup  string
	dropoff string
	distance float64
}

func (r *BaseRide) PrintDetails(fare float64) {
	fmt.Printf("Ride ID: %d\nPickup: %s\nDropoff: %s\nDistance: %.2f miles\nFare: $%.2f\n--------------------------------\n", r.id, r.pickup, r.dropoff, r.distance, fare)
}

type StandardRide struct {
	BaseRide
}

func NewStandardRide(pickup, dropoff string, distance float64) *StandardRide {
	rideIDMutex.Lock()
	rideID := rideIDCounter
	rideIDCounter++
	rideIDMutex.Unlock()

	return &StandardRide{BaseRide{id: rideID, pickup: pickup, dropoff: dropoff, distance: distance}}
}

func (r *StandardRide) CalculateFare() float64 {
	return r.distance * 1.5
}

func (r *StandardRide) PrintDetails() {
	r.BaseRide.PrintDetails(r.CalculateFare())
}

type PremiumRide struct {
	BaseRide
}

func NewPremiumRide(pickup, dropoff string, distance float64) *PremiumRide {
	rideIDMutex.Lock()
	rideID := rideIDCounter
	rideIDCounter++
	rideIDMutex.Unlock()

	return &PremiumRide{BaseRide{id: rideID, pickup: pickup, dropoff: dropoff, distance: distance}}
}

func (r *PremiumRide) CalculateFare() float64 {
	return r.distance * 3.0
}

func (r *PremiumRide) PrintDetails() {
	r.BaseRide.PrintDetails(r.CalculateFare())
}

// ===== Rider =====
type Rider struct {
	id     int
	name   string
	rides  []Ride
}

func NewRider(name string) *Rider {
	riderIDMutex.Lock()
	id := riderIDCounter
	riderIDCounter++
	riderIDMutex.Unlock()

	return &Rider{id: id, name: name}
}

func (r *Rider) RequestRide(ride Ride) {
	r.rides = append(r.rides, ride)
	fmt.Printf("Rider %s requested a ride\n", r.name)
}

// ===== Driver =====
type Driver struct {
	id     int
	name   string
	rating int
	rides  []Ride
}

func NewDriver(name string, rating int) *Driver {
	driverIDMutex.Lock()
	id := driverIDCounter
	driverIDCounter++
	driverIDMutex.Unlock()

	return &Driver{id: id, name: name, rating: rating}
}

func (d *Driver) AddRide(ride Ride) {
	d.rides = append(d.rides, ride)
	fmt.Printf("Driver %s assigned a ride\n", d.name)
}

// ===== SharingRideSystem =====
type SharingRideSystem struct {
	riderQueue  chan *Rider
	driverQueue chan *Driver

	driverMap map[int]*Driver
	riderMap  map[int]*Rider

	mu sync.Mutex
}

func NewSharingRideSystem(queueSize int) *SharingRideSystem {
	return &SharingRideSystem{
		riderQueue:  make(chan *Rider, queueSize),
		driverQueue: make(chan *Driver, queueSize),
		driverMap:   make(map[int]*Driver),
		riderMap:    make(map[int]*Rider),
	}
}

func (s *SharingRideSystem) AddRider(r *Rider) {
	s.mu.Lock()
	s.riderMap[r.id] = r
	s.mu.Unlock()
	s.riderQueue <- r
}

func (s *SharingRideSystem) AddDriver(d *Driver) {
	s.mu.Lock()
	s.driverMap[d.id] = d
	s.mu.Unlock()
	s.driverQueue <- d
}

func (s *SharingRideSystem) MatchWorker(id int, rideType int) {
	for {
		rider := <-s.riderQueue
		driver := <-s.driverQueue

		var ride Ride
		if rideType == 1 {
			ride = NewStandardRide("A", "B", 10)
		} else {
			ride = NewPremiumRide("A", "B", 10)
		}

		rider.RequestRide(ride)
		driver.AddRide(ride)

		fmt.Printf("[Worker %d] Matched Rider %s with Driver %s\n", id, rider.name, driver.name)
	}
}

// ===== Main Function =====
// This function initializes the ride-sharing system and starts the worker goroutines.
// It also adds drivers and riders to the system, simulating ride requests and assignments.
// The main function is the entry point of the program.
// It creates a new SharingRideSystem instance, starts multiple worker goroutines for matching,
func main() {
	system := NewSharingRideSystem(10)

	// Start 3 matching worker goroutines
	for i := 1; i <= 3; i++ {
		go system.MatchWorker(i, i%2+1)
	}

	// Add drivers and riders
	for i := 1; i <= 5; i++ {
		system.AddDriver(NewDriver(fmt.Sprintf("Driver-%d", i), 5))
		system.AddRider(NewRider(fmt.Sprintf("Rider-%d", i)))
		time.Sleep(500 * time.Millisecond)
	}

	time.Sleep(5 * time.Second) // let workers finish
}

