package in.ashokit.rest;
import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.ashokit.binding.CustomerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
@RestController
public class CustomerRestController
{
	@GetMapping("/event")
	public ResponseEntity<Mono<CustomerEvent>> getCustomerEvent()
	{
		CustomerEvent event = new CustomerEvent("Smith", new Date());
		Mono<CustomerEvent> monoObj = Mono.just(event);
		return new ResponseEntity<Mono<CustomerEvent>>(monoObj,HttpStatus.OK);
	}
	
	@GetMapping(value="/events",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<CustomerEvent>> getCustomerEvents()
	{
		CustomerEvent event = new CustomerEvent("Smith", new Date());
		Stream<CustomerEvent> customerStream = Stream.generate(() -> event);
		Flux<CustomerEvent> cFlux = Flux.fromStream(customerStream);
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(5));
		Flux<Tuple2<Long, CustomerEvent>> zip = Flux.zip(interval, cFlux);
		Flux<CustomerEvent> fluxMap = zip.map(Tuple2::getT2);
		return new ResponseEntity<Flux<CustomerEvent>>(fluxMap,HttpStatus.OK);

	}

}
