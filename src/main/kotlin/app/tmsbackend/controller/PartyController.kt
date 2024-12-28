package app.tmsbackend.controller

import app.tmsbackend.model.Party
import app.tmsbackend.model.ListPartiesInput
import app.tmsbackend.service.PartyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("/api/v1/parties")
@RestController
class PartyController(
    private val partyService: PartyService
) {

    @PostMapping("/create")
    fun createParty(@RequestBody party: Party): ResponseEntity<Party> {
        val createdParty = partyService.createParty(party)
        return ResponseEntity.ok(createdParty)
    }

    @PostMapping("/update")
    fun updateParty(@RequestBody party: Party): ResponseEntity<Party> {
        val updatedParty = partyService.updateParty(party)
        return ResponseEntity.ok(updatedParty)
    }

    @GetMapping("/{id}")
    fun getParty(@PathVariable id: String): ResponseEntity<Party> {
        val party = partyService.getParty(id)
        return ResponseEntity.ok(party)
    }

    @PostMapping("/list")
    fun listParties(
        @RequestBody listPartiesInput: ListPartiesInput
    ): ResponseEntity<List<Party>> {
        val parties = partyService.listParties(
            search = listPartiesInput.search,
            page = listPartiesInput.page,
            size = listPartiesInput.size
        )
        return ResponseEntity.ok(parties)
    }

    @GetMapping("/listAll")
    fun listAllParties(): ResponseEntity<List<Party>> {
        val parties = partyService.listAllParties()
        return ResponseEntity.ok(parties)
    }
}