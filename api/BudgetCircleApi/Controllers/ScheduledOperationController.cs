namespace BudgetCircleApi.Controllers
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using Microsoft.AspNetCore.Authorization;
    using Microsoft.AspNetCore.Mvc;
    using System;
    using System.Linq;
    using System.Security.Claims;
    using System.Threading.Tasks;

    [ApiController]
    [Route("[controller]")]
    public class ScheduledOperationController : Controller
    {
        private readonly IScheduledOperationsService _scheduledOperationsServices;

        public ScheduledOperationController(IScheduledOperationsService scheduledOperationsServices)
        {
            _scheduledOperationsServices = scheduledOperationsServices;
        }

        [HttpGet]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> GetAllOperations([FromQuery] bool isExpense)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var result = await Task.Run(() => _scheduledOperationsServices.GetScheduledOperations(userId, isExpense));

                return Ok(result);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpPut]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> AddOperation([FromQuery] ScheduledOperationModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState.Values.SelectMany(e => e.Errors.Select(er => er.ErrorMessage)));
            }

            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var response = await _scheduledOperationsServices.AddScheduledOperation(userId, model);

                if (response is ErrorResponse)
                {
                    return BadRequest(response);
                }

                return Ok(response);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpDelete]
        [Route("{id:int}")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> RemoveOperation([FromRoute] int id)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _scheduledOperationsServices.RemoveScheduledOperation(userId, id);

                return NoContent();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }


        [HttpPost]
        [Route("{id:int}")]
        [Authorize(AuthenticationSchemes = "Bearer")]
        public async Task<IActionResult> UpdateOperation([FromRoute] int id, [FromQuery] ScheduledOperationModel model)
        {
            try
            {
                string userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                await _scheduledOperationsServices.UpdateScheduledOperation(userId, id, model);

                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }
    }
}
