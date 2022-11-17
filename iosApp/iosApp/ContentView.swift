import SwiftUI
import shared

struct ContentView: View {
    @State var location: Any? = nil
    @ObservedObject var vm = LocationObservableObject(location: nil)
    
    init() {
        vm.startObservingLocation()
    }

	var body: some View {
        if (vm.location == nil) {
            Text("loading . . .")
        } else {
            let string = String(describing: vm.location!)
            Text(string)
        }
	}
}

class LocationObservableObject : ObservableObject {
    @Published var location: Location?
    
    
    init(location: Location?) {
        updateLocation(location: location)
    }
    
    private func updateLocation(location: Location?) {
        DispatchQueue.main.async {
            self.location = location
        }
    }
    
    func startObservingLocation() {
        KmmLocationProvider.companion.createInstance().getLocation().collect(collector: Collector<Location?> { emittedLocation in
            print("Log: emittedLocation = \(String(describing: emittedLocation))")
            self.updateLocation(location: emittedLocation)
        }) { (error) in

        }
        KmmLocationProvider.companion.createInstance().getLocation().collectFlow()
    }
}

